--CREATE DATABASE trust WITH ENCODING='UTF8' CONNECTION LIMIT=-1;
-- Extensions
CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Schemas
CREATE SCHEMA history;

-- Public functions

CREATE OR REPLACE FUNCTION public.f_for_trg_control_changes()
  RETURNS trigger AS
$BODY$
BEGIN
    IF (TG_OP = 'UPDATE') THEN
        IF (NEW.rowversion != OLD.rowversion) THEN
            RAISE EXCEPTION 'row_has_different_version';
        END IF;
        IF (NEW.action_code != 'd') THEN
            NEW.action_code := 'u';
        END IF;
        IF OLD.rowversion > 200000000 THEN
            NEW.rowversion = 1;
        ELSE
            NEW.rowversion = OLD.rowversion + 1;
        END IF;
    ELSIF (TG_OP = 'INSERT') THEN
        NEW.action_code := 'i';
        NEW.rowversion = 1;
    END IF;
    NEW.action_time := now();
    IF NEW.action_user is null then
      NEW.action_user = 'db:' || current_user;
    END IF;
    RETURN NEW;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE COST 100;
ALTER FUNCTION public.f_for_trg_control_changes() OWNER TO postgres;
COMMENT ON FUNCTION public.f_for_trg_control_changes() IS 'This function is used to implement optimistic locking on the main data tables. The control is done using rowversion field. It also sets action code, time and user, triggered the change. Row version will be incremented automatically';

CREATE OR REPLACE FUNCTION public.f_for_trg_track_history()
  RETURNS trigger AS
$BODY$
DECLARE
    table_name_history varchar;
    insert_col_part varchar;
    values_part varchar;
BEGIN
    table_name_history = 'history.' || TG_TABLE_NAME;
    insert_col_part = (select string_agg(column_name, ',') 
      from information_schema.columns  
      where table_schema= TG_TABLE_SCHEMA and table_name = TG_TABLE_NAME);
    values_part = '$1.' || replace(insert_col_part, ',' , ',$1.');

    IF (TG_OP = 'DELETE') THEN
        OLD.action_code := 'd';
    END IF;

    EXECUTE 'INSERT INTO ' || table_name_history || '(' || insert_col_part || ') SELECT ' || values_part || ';' USING OLD;
    
    IF (TG_OP = 'DELETE') THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.f_for_trg_track_history() OWNER TO postgres;
COMMENT ON FUNCTION public.f_for_trg_track_history() IS 'This function is used to record changes on the main table into the history table.';

CREATE OR REPLACE FUNCTION public.get_translation(
    mixed_value character varying,
    language_code character varying)
  RETURNS character varying AS
$BODY$
DECLARE
  delimiter_word varchar;
  language_index integer;
  result varchar;
BEGIN
  if mixed_value is null then
    return mixed_value;
  end if;
  
  delimiter_word = '::::';
  language_index = (select item_order from public.ref_language where lower(code)=lower(language_code));
  
  if language_index is null then
    language_index = (select item_order from public.ref_language where is_default limit 1);
  end if;

  if language_index is null then
    return mixed_value;
  end if;
  
  result = split_part(mixed_value, delimiter_word, language_index);
  
  if result is null or result = '' then
    language_index = (select item_order from public.ref_language where is_default limit 1);
    
    if language_index is null then
      return mixed_value;
    end if;
    
    result = split_part(mixed_value, delimiter_word, language_index);
    
    if result is null or result = '' then
      result = mixed_value;
    end if;
    
  end if;
  return result;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.get_translation(character varying, character varying)
  OWNER TO postgres;
COMMENT ON FUNCTION public.get_translation(character varying, character varying) IS 'This function is used to translate the values that are supposed to be multilingual like the reference data values';

-- Users

CREATE TABLE public.appuser
(
  id character varying(40) NOT NULL, 
  username character varying(40) NOT NULL, 
  passwd character varying(255) NOT NULL,
  first_name character varying(30) NOT NULL, 
  last_name character varying(30) NOT NULL, 
  email character varying(40),
  mobile_number character varying(20),
  active boolean NOT NULL DEFAULT true,
  description character varying(255),
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT user_pkey PRIMARY KEY (id),
  CONSTRAINT user_email_unique UNIQUE (email),
  CONSTRAINT user_username_unique UNIQUE (username)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.appuser OWNER TO postgres;
COMMENT ON TABLE public.appuser
  IS 'Users list who can acceess the application.';
COMMENT ON COLUMN public.appuser.id IS 'Unuque identifier.';
COMMENT ON COLUMN public.appuser.username IS 'User name.';
COMMENT ON COLUMN public.appuser.passwd IS 'User password.';
COMMENT ON COLUMN public.appuser.first_name IS 'First name.';
COMMENT ON COLUMN public.appuser.last_name IS 'Last name.';
COMMENT ON COLUMN public.appuser.email IS 'Email address.';
COMMENT ON COLUMN public.appuser.mobile_number IS 'Mobile number.';
COMMENT ON COLUMN public.appuser.active IS 'Flag, indicating that user is active or not active for login.';
COMMENT ON COLUMN public.appuser.description IS 'Additional description of the user.';
COMMENT ON COLUMN public.appuser.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.appuser.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.appuser.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.appuser.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.appuser
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.appuser
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.appuser
(
  id character varying(40), 
  username character varying(40), 
  passwd character varying(255),
  first_name character varying(30), 
  last_name character varying(30), 
  email character varying(40),
  mobile_number character varying(20),
  active boolean,
  description character varying(255),
  rowversion integer,
  action_code character(1),
  action_user character varying(50),
  action_time timestamp without time zone,
  recording_time timestamp without time zone NOT NULL DEFAULT now()
)
WITH (
  OIDS=FALSE
);
ALTER TABLE history.appuser OWNER TO postgres;

-- Role

CREATE TABLE public.approle
(
  code character varying(20) NOT NULL,
  role_name character varying(500) NOT NULL,
  description character varying(2000),
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT approle_pkey PRIMARY KEY (code),
  CONSTRAINT approle_val_unique UNIQUE (role_name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.approle OWNER TO postgres;
COMMENT ON TABLE public.approle IS 'Contains the list of application security roles used to define access to different parts of the application';
COMMENT ON COLUMN public.approle.code IS 'Code of the security role.';
COMMENT ON COLUMN public.approle.role_name IS 'Name of the role.';
COMMENT ON COLUMN public.approle.description IS 'Role description.';
COMMENT ON COLUMN public.approle.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.approle.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.approle.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.approle.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.approle
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.approle
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.approle
(
  code character varying(20),
  role_name character varying(500),
  description character varying(2000),
  rowversion integer,
  action_code character(1),
  action_user character varying(50),
  action_time timestamp without time zone,
  recording_time timestamp without time zone NOT NULL DEFAULT now()
)
WITH (
  OIDS=FALSE
);
ALTER TABLE history.approle OWNER TO postgres;

-- Group

CREATE TABLE public.appgroup
(
  id character varying(40) NOT NULL,
  group_name character varying(500) NOT NULL,
  description character varying(1000),
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT appgroup_pkey PRIMARY KEY (id),
  CONSTRAINT appgroup_name_unique UNIQUE (group_name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.appgroup OWNER TO postgres;
COMMENT ON TABLE public.appgroup IS 'Contains the list of groups for grouping application users.';
COMMENT ON COLUMN public.appgroup.id IS 'Identifier of the group.';
COMMENT ON COLUMN public.appgroup.group_name IS 'Name of the group.';
COMMENT ON COLUMN public.appgroup.description IS 'Group description.';
COMMENT ON COLUMN public.appgroup.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.appgroup.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.appgroup.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.appgroup.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.appgroup
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.appgroup
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.appgroup
(
  id character varying(40),
  group_name character varying(500),
  description character varying(1000),
  rowversion integer,
  action_code character(1),
  action_user character varying(50),
  action_time timestamp without time zone,
  recording_time timestamp without time zone NOT NULL DEFAULT now()
)
WITH (
  OIDS=FALSE
);
ALTER TABLE history.appgroup OWNER TO postgres;

-- Role to group relation

CREATE TABLE public.approle_appgroup
(
  role_code character varying(20) NOT NULL, 
  group_id character varying(40) NOT NULL,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT approle_appgroup_pkey PRIMARY KEY (role_code, group_id),
  CONSTRAINT approle_appgroup_appgroup_id_fk118 FOREIGN KEY (group_id)
      REFERENCES public.appgroup (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT approle_appgroup_approle_code_fk117 FOREIGN KEY (role_code)
      REFERENCES public.approle (code) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.approle_appgroup OWNER TO postgres;
COMMENT ON TABLE public.approle_appgroup IS 'Associates application security roles to the groups.';
COMMENT ON COLUMN public.approle_appgroup.role_code IS 'Code of security role.';
COMMENT ON COLUMN public.approle_appgroup.group_id IS 'Identifier of the group.';
COMMENT ON COLUMN public.approle_appgroup.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.approle_appgroup.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.approle_appgroup.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.approle_appgroup.action_time IS 'Date and time, of the action.';

CREATE INDEX approle_appgroup_appgroup_id_fk118_ind
  ON public.approle_appgroup
  USING btree
  (group_id COLLATE pg_catalog."default");

CREATE INDEX approle_appgroup_approle_code_fk117_ind
  ON public.approle_appgroup
  USING btree
  (role_code COLLATE pg_catalog."default");
  
CREATE TRIGGER __track_changes
  BEFORE INSERT OR UPDATE
  ON public.approle_appgroup
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.approle_appgroup
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.approle_appgroup
(
  role_code character varying(20), 
  group_id character varying(40),
  rowversion integer,
  action_code character(1),
  action_user character varying(50),
  action_time timestamp without time zone,
  recording_time timestamp without time zone NOT NULL DEFAULT now()
)
WITH (
  OIDS=FALSE
);
ALTER TABLE history.approle_appgroup OWNER TO postgres;

-- User to group relation

CREATE TABLE public.appuser_appgroup
(
  user_id character varying(40) NOT NULL, 
  group_id character varying(40) NOT NULL, 
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT appuser_appgroup_pkey PRIMARY KEY (user_id, group_id),
  CONSTRAINT appuser_appgroup_appgroup_id_fk120 FOREIGN KEY (group_id)
      REFERENCES public.appgroup (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT appuser_appgroup_appuser_id_fk119 FOREIGN KEY (user_id)
      REFERENCES public.appuser (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.appuser_appgroup OWNER TO postgres;

COMMENT ON TABLE public.appuser_appgroup IS 'Associates users to groups.';
COMMENT ON COLUMN public.appuser_appgroup.user_id IS 'Identifier of the user.';
COMMENT ON COLUMN public.appuser_appgroup.group_id IS 'Identifier of the group.';
COMMENT ON COLUMN public.appuser_appgroup.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.appuser_appgroup.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.appuser_appgroup.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.appuser_appgroup.action_time IS 'Date and time, of the action.';

CREATE INDEX appuser_appgroup_appgroup_id_fk120_ind
  ON public.appuser_appgroup
  USING btree
  (group_id COLLATE pg_catalog."default");

CREATE INDEX appuser_appgroup_appuser_id_fk119_ind
  ON public.appuser_appgroup
  USING btree
  (user_id COLLATE pg_catalog."default");

CREATE TRIGGER __track_changes
  BEFORE INSERT OR UPDATE
  ON public.appuser_appgroup
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.appuser_appgroup
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.appuser_appgroup
(
  user_id character varying(40), 
  group_id character varying(40), 
  rowversion integer,
  action_code character(1),
  action_user character varying(50),
  action_time timestamp without time zone,
  recording_time timestamp without time zone NOT NULL DEFAULT now()
)
WITH (
  OIDS=FALSE
);
ALTER TABLE history.appuser_appgroup OWNER TO postgres;

-- User-role view

CREATE OR REPLACE VIEW public.user_role AS 
 SELECT u.username, rg.role_code AS rolename
   FROM public.appuser u
     JOIN public.appuser_appgroup ug ON u.id::text = ug.user_id::text AND u.active
     JOIN public.approle_appgroup rg ON ug.group_id::text = rg.group_id::text;

ALTER TABLE public.user_role OWNER TO postgres;
COMMENT ON VIEW public.user_role IS 'Determines the application security roles assigned to each user.';

-- Language

CREATE TABLE public.ref_language
(
  code character varying(7) NOT NULL, 
  val character varying(250) NOT NULL, 
  active boolean NOT NULL DEFAULT TRUE, 
  is_default boolean NOT NULL DEFAULT FALSE, 
  item_order integer NOT NULL DEFAULT 1,
  ltr boolean NOT NULL DEFAULT true, 
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_language_pkey PRIMARY KEY (code),
  CONSTRAINT ref_language_display_value_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_language OWNER TO postgres;
COMMENT ON TABLE public.ref_language IS 'Lists of languages supported in the system';
COMMENT ON COLUMN public.ref_language.code IS 'Langauge code.';
COMMENT ON COLUMN public.ref_language.val IS 'Language name.';
COMMENT ON COLUMN public.ref_language.active IS 'Boolean flag indicating if the language is active or not.';
COMMENT ON COLUMN public.ref_language.is_default IS 'Boolean flag indicating if the is set as default.';
COMMENT ON COLUMN public.ref_language.item_order IS 'Order of the langages in the list. This order is very important for localization and it should not be changed, without changing localizable strings.';
COMMENT ON COLUMN public.ref_language.ltr IS 'Boolean flag indicating text direction for the language. If true, then left to right should applied, otherwise right to left.';
COMMENT ON COLUMN public.ref_language.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_language.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_language.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_language.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_language
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_language
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_language
(
  code character varying(7), 
  val character varying(250), 
  active boolean, 
  is_default boolean, 
  item_order integer,
  ltr boolean, 
  rowversion integer,
  action_code character(1),
  action_user character varying(50),
  action_time timestamp without time zone,
  recording_time timestamp without time zone NOT NULL DEFAULT now()
)
WITH (
  OIDS=FALSE
);
ALTER TABLE history.ref_language OWNER TO postgres;

-- System version

CREATE TABLE public.version
(
  num character varying(50) NOT NULL, 
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT version_pkey PRIMARY KEY (num)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.version OWNER TO postgres;
COMMENT ON TABLE public.version
  IS 'Contains version number of the system. It can be used to control that client application is matching to the recorded version of the system.';
COMMENT ON COLUMN public.version.num IS 'version number.';
COMMENT ON COLUMN public.version.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.version.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.version.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.version.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.version
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.version
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();
  
CREATE TABLE history.version
(
  num character varying(50), 
  rowversion integer,
  action_code character(1),
  action_user character varying(50),
  action_time timestamp without time zone,
  recording_time timestamp without time zone NOT NULL DEFAULT now()
)
WITH (
  OIDS=FALSE
);
ALTER TABLE history.version OWNER TO postgres;

-- System settings

CREATE TABLE public.setting
(
  id character varying(50) NOT NULL,
  val character varying(2000) NOT NULL, 
  active boolean NOT NULL DEFAULT TRUE,
  description character varying(555) NOT NULL, 
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT setting_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.setting OWNER TO postgres;
COMMENT ON TABLE public.setting IS 'Contains different system settings.';
COMMENT ON COLUMN public.setting.id IS 'Identifier of the setting';
COMMENT ON COLUMN public.setting.val IS 'Value for the setting.';
COMMENT ON COLUMN public.setting.active IS 'Indicates if the setting is active or not.';
COMMENT ON COLUMN public.setting.description IS 'Description of the setting. ';
COMMENT ON COLUMN public.setting.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.setting.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.setting.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.setting.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.setting
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.setting
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.setting
(
  id character varying(50),
  val character varying(2000), 
  active boolean,
  description character varying(555), 
  rowversion integer,
  action_code character(1),
  action_user character varying(50),
  action_time timestamp without time zone,
  recording_time timestamp without time zone NOT NULL DEFAULT now()
)
WITH (
  OIDS=FALSE
);
ALTER TABLE history.setting OWNER TO postgres;

-- Document type

CREATE TABLE public.ref_doc_type
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_doc_type_pkey PRIMARY KEY (code),
  CONSTRAINT ref_doc_type_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_doc_type OWNER TO postgres;
COMMENT ON TABLE public.ref_doc_type IS 'Contains the list of document types';
COMMENT ON COLUMN public.ref_doc_type.code IS 'Document type code.';
COMMENT ON COLUMN public.ref_doc_type.val IS 'Document type name.';
COMMENT ON COLUMN public.ref_doc_type.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_doc_type.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_doc_type.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_doc_type.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_doc_type.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_doc_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_doc_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_doc_type
(
  code character varying(20),
  val character varying(500),
  active boolean,
  rowversion integer,
  action_code character(1),
  action_user character varying(50),
  action_time timestamp without time zone,
  recording_time timestamp without time zone NOT NULL DEFAULT now()
)
WITH (
  OIDS=FALSE
);
ALTER TABLE history.ref_doc_type OWNER TO postgres;

-- Add initial data

INSERT INTO public.approle(code, role_name, description) VALUES ('Admin', 'Administration', 'Used for managing users, groups and system settings.');
INSERT INTO public.approle(code, role_name, description) VALUES ('RefDataManager', 'Reference data management', 'Used for managing reference data tables.');
INSERT INTO public.approle(code, role_name, description) VALUES ('Reader', 'Reader', 'Used for accessing the system in read-only mode. It is not suppoed to do any changes.');

INSERT INTO public.appgroup(id, group_name, description) VALUES ('admin-group', 'Administrators', 'Group of administrators');
INSERT INTO public.appgroup(id, group_name, description) VALUES ('reader-group', 'Readers', 'Group of read-only users');

INSERT INTO public.approle_appgroup(role_code, group_id) VALUES ('Admin', 'admin-group');
INSERT INTO public.approle_appgroup(role_code, group_id) VALUES ('RefDataManager', 'admin-group');
INSERT INTO public.approle_appgroup(role_code, group_id) VALUES ('Reader', 'reader-group');

-- sha256 hash. password admin
INSERT INTO public.appuser(id, username, passwd, first_name, last_name, email, mobile_number, active, description)
  VALUES ('admin-user', 'admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'User', 'Admin', null, '', true, 'System administrator');

-- sha5256 hash. password reader
INSERT INTO public.appuser(id, username, passwd, first_name, last_name, email, mobile_number, active, description)
  VALUES ('reader-user', 'reader', '3d0941964aa3ebdcb00ccef58b1bb399f9f898465e9886d5aec7f31090a0fb30', 'User', 'Reader', null, '', true, 'Read-only user');

INSERT INTO public.appuser_appgroup(user_id, group_id) VALUES ('admin-user', 'admin-group');
INSERT INTO public.appuser_appgroup(user_id, group_id) VALUES ('reader-user', 'reader-group');

INSERT INTO public.ref_language(code, val, active, is_default, item_order, ltr) VALUES ('en', 'English', true, true, 1, true);
INSERT INTO public.ref_language(code, val, active, is_default, item_order, ltr) VALUES ('sw', 'Kiswahili', true, false, 2, true);

-- Settings
INSERT INTO public.setting(id, val, active, description) VALUES ('version', '0.1', 't', 'Current version of the database. It is used to check compatibility with application version. If versions does not match, an exception will be thrown');


-- Doc types
INSERT INTO public.ref_doc_type(code, val) VALUES ('irs', 'Informal Receipt of Sale::::Hati ya manunuzi isiyo rasmi');
INSERT INTO public.ref_doc_type(code, val) VALUES ('frs', 'Formal Receipt of Sale::::Hati ya manunuzi iliyo rasmi');
INSERT INTO public.ref_doc_type(code, val) VALUES ('al', 'Letter of Allocation::::Barua ya mgao');
INSERT INTO public.ref_doc_type(code, val) VALUES ('pd', 'Probate Document::::Hati ya Mirathi');
INSERT INTO public.ref_doc_type(code, val) VALUES ('oth', 'Other::::Nyingine');


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

-- Transaction type

CREATE TABLE public.ref_transaction_type
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_transaction_type_pkey PRIMARY KEY (code),
  CONSTRAINT ref_transaction_type_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_transaction_type OWNER TO postgres;
COMMENT ON TABLE public.ref_transaction_type IS 'Contains a list of transaction types';
COMMENT ON COLUMN public.ref_transaction_type.code IS 'Transaction type code.';
COMMENT ON COLUMN public.ref_transaction_type.val IS 'Transaction type name.';
COMMENT ON COLUMN public.ref_transaction_type.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_transaction_type.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_transaction_type.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_transaction_type.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_transaction_type.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_transaction_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_transaction_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_transaction_type
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
ALTER TABLE history.ref_transaction_type OWNER TO postgres;

-- Application type group

CREATE TABLE public.ref_app_type_group
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_app_type_group_pkey PRIMARY KEY (code),
  CONSTRAINT ref_app_type_group_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_app_type_group OWNER TO postgres;
COMMENT ON TABLE public.ref_app_type_group IS 'Contains a list of application type groups, which is for logical grouping of application types and can be used for arranging main menu.';
COMMENT ON COLUMN public.ref_app_type_group.code IS 'Application type group code.';
COMMENT ON COLUMN public.ref_app_type_group.val IS 'Application type group name.';
COMMENT ON COLUMN public.ref_app_type_group.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_app_type_group.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_app_type_group.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_app_type_group.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_app_type_group.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_app_type_group
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_app_type_group
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_app_type_group
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
ALTER TABLE history.ref_app_type_group OWNER TO postgres;

-- Application type

CREATE TABLE public.ref_app_type
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  app_type_group_code character varying(20) NOT NULL,
  transaction_type_code character varying(20) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_app_type_pkey PRIMARY KEY (code),
  CONSTRAINT ref_app_type_val_unique UNIQUE (val),
  CONSTRAINT ref_app_type_app_group_type_fk FOREIGN KEY (app_type_group_code)
      REFERENCES public.ref_app_type_group (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ref_app_type_transaction_type_fk FOREIGN KEY (transaction_type_code)
      REFERENCES public.ref_transaction_type (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_app_type OWNER TO postgres;
COMMENT ON TABLE public.ref_app_type IS 'Contains a list of application types.';
COMMENT ON COLUMN public.ref_app_type.code IS 'Application type code.';
COMMENT ON COLUMN public.ref_app_type.val IS 'Application type name.';
COMMENT ON COLUMN public.ref_app_type.app_type_group_code IS 'Application type group code.';
COMMENT ON COLUMN public.ref_app_type.transaction_type_code IS 'Transaction type code.';
COMMENT ON COLUMN public.ref_app_type.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_app_type.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_app_type.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_app_type.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_app_type.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_app_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_app_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_app_type
(
  code character varying(20),
  val character varying(500),
  app_type_group_code character varying(20),
  transaction_type_code character varying(20),
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
ALTER TABLE history.ref_app_type OWNER TO postgres;

-- Right type group

CREATE TABLE public.ref_right_type_group
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_right_type_group_pkey PRIMARY KEY (code),
  CONSTRAINT ref_right_type_group_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_right_type_group OWNER TO postgres;
COMMENT ON TABLE public.ref_right_type_group IS 'Contains list of right type groups, such as ownership, restriction, responsibility';
COMMENT ON COLUMN public.ref_right_type_group.code IS 'Right type group code.';
COMMENT ON COLUMN public.ref_right_type_group.val IS 'Right type group name.';
COMMENT ON COLUMN public.ref_right_type_group.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_right_type_group.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_right_type_group.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_right_type_group.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_right_type_group.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_right_type_group
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_right_type_group
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_right_type_group
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
ALTER TABLE history.ref_right_type_group OWNER TO postgres;

-- Right type

CREATE TABLE public.ref_right_type
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  right_type_group_code character varying(20) NOT NULL,
  allow_multiple boolean NOT NULL DEFAULT false,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_right_type_pkey PRIMARY KEY (code),
  CONSTRAINT ref_right_type_val_unique UNIQUE (val),
  CONSTRAINT ref_right_type_ref_right_type_group_fk FOREIGN KEY (right_type_group_code)
      REFERENCES public.ref_right_type_group (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_right_type OWNER TO postgres;
COMMENT ON TABLE public.ref_right_type IS 'Contains list of right types.';
COMMENT ON COLUMN public.ref_right_type.code IS 'Right type code.';
COMMENT ON COLUMN public.ref_right_type.val IS 'Right type name.';
COMMENT ON COLUMN public.ref_right_type.right_type_group_code IS 'Right type group code.';
COMMENT ON COLUMN public.ref_right_type.allow_multiple IS 'Indicates whether multiple registrations of this right type can be done for one parcel.';
COMMENT ON COLUMN public.ref_right_type.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_right_type.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_right_type.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_right_type.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_right_type.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_right_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_right_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_right_type
(
  code character varying(20),
  val character varying(500),
  right_type_group_code character varying(20),
  allow_multiple boolean,
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
ALTER TABLE history.ref_right_type OWNER TO postgres;

-- Application type and right type relations

CREATE TABLE public.ref_app_type_right_type
(
  app_type_code character varying(20) NOT NULL,
  right_type_code character varying(20) NOT NULL,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_app_type_right_type_pkey PRIMARY KEY (app_type_code, right_type_code),
  CONSTRAINT ref_app_type_right_type_ref_app_type_fk FOREIGN KEY (app_type_code)
      REFERENCES public.ref_app_type (code) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT ref_app_type_right_type_ref_right_type_fk FOREIGN KEY (right_type_code)
      REFERENCES public.ref_right_type (code) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_app_type_right_type OWNER TO postgres;
COMMENT ON TABLE public.ref_app_type_right_type IS 'Contains relations between application types and right types.';
COMMENT ON COLUMN public.ref_app_type_right_type.app_type_code IS 'Application type code.';
COMMENT ON COLUMN public.ref_app_type_right_type.right_type_code IS 'Right type code.';
COMMENT ON COLUMN public.ref_app_type_right_type.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_app_type_right_type.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_app_type_right_type.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_app_type_right_type.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_app_type_right_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_app_type_right_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_app_type_right_type
(
  app_type_code character varying(20),
  right_type_code character varying(20),
  rowversion integer,
  action_code character(1),
  action_user character varying(50),
  action_time timestamp without time zone,
  recording_time timestamp without time zone NOT NULL DEFAULT now()
)
WITH (
  OIDS=FALSE
);
ALTER TABLE history.ref_app_type_right_type OWNER TO postgres;

-- Application status

CREATE TABLE public.ref_app_status
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_app_status_pkey PRIMARY KEY (code),
  CONSTRAINT ref_app_status_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_app_status OWNER TO postgres;
COMMENT ON TABLE public.ref_app_status IS 'Contains list of application statuses';
COMMENT ON COLUMN public.ref_app_status.code IS 'Application status code.';
COMMENT ON COLUMN public.ref_app_status.val IS 'Application status name.';
COMMENT ON COLUMN public.ref_app_status.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_app_status.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_app_status.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_app_status.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_app_status.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_app_status
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_app_status
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_app_status
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
ALTER TABLE history.ref_app_status OWNER TO postgres;

-- Registration statuses

CREATE TABLE public.ref_reg_status
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_reg_status_pkey PRIMARY KEY (code),
  CONSTRAINT ref_reg_status_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_reg_status OWNER TO postgres;
COMMENT ON TABLE public.ref_reg_status IS 'Contains list of registration statuses, applicable to rights and administrative units.';
COMMENT ON COLUMN public.ref_reg_status.code IS 'Registration status code.';
COMMENT ON COLUMN public.ref_reg_status.val IS 'Registration status name.';
COMMENT ON COLUMN public.ref_reg_status.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_reg_status.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_reg_status.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_reg_status.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_reg_status.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_reg_status
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_reg_status
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_reg_status
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
ALTER TABLE history.ref_reg_status OWNER TO postgres;

-- Regions

CREATE TABLE public.ref_region
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_region_pkey PRIMARY KEY (code),
  CONSTRAINT ref_region_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_region OWNER TO postgres;
COMMENT ON TABLE public.ref_region IS 'Contains list of regions';
COMMENT ON COLUMN public.ref_region.code IS 'Region code.';
COMMENT ON COLUMN public.ref_region.val IS 'Region name.';
COMMENT ON COLUMN public.ref_region.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_region.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_region.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_region.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_region.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_region
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_region
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_region
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
ALTER TABLE history.ref_region OWNER TO postgres;

-- Districts

CREATE TABLE public.ref_district
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  region_code character varying(20) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_district_pkey PRIMARY KEY (code),
  CONSTRAINT ref_district_val_unique UNIQUE (val),
  CONSTRAINT ref_district_ref_region_fk FOREIGN KEY (region_code)
      REFERENCES public.ref_region (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_district OWNER TO postgres;
COMMENT ON TABLE public.ref_district IS 'Contains list of districts';
COMMENT ON COLUMN public.ref_district.code IS 'District code.';
COMMENT ON COLUMN public.ref_district.val IS 'District name.';
COMMENT ON COLUMN public.ref_district.region_code IS 'Region code.';
COMMENT ON COLUMN public.ref_district.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_district.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_district.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_district.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_district.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_district
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_district
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_district
(
  code character varying(20),
  val character varying(500),
  region_code character varying(20),
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
ALTER TABLE history.ref_district OWNER TO postgres;

-- Villages

CREATE TABLE public.ref_village
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  district_code character varying(20) NOT NULL,
  address character varying(255),
  chairman character varying(255),
  executive_officer character varying(255),
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_village_pkey PRIMARY KEY (code),
  CONSTRAINT ref_village_val_unique UNIQUE (district_code, val),
  CONSTRAINT ref_village_ref_district_fk FOREIGN KEY (district_code)
      REFERENCES public.ref_district (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_village OWNER TO postgres;
COMMENT ON TABLE public.ref_village IS 'Contains list of villages';
COMMENT ON COLUMN public.ref_village.code IS 'Village code.';
COMMENT ON COLUMN public.ref_village.val IS 'Village name.';
COMMENT ON COLUMN public.ref_village.district_code IS 'District code.';
COMMENT ON COLUMN public.ref_village.address IS 'Village postal address.';
COMMENT ON COLUMN public.ref_village.chairman IS 'Village chairman name.';
COMMENT ON COLUMN public.ref_village.executive_officer IS 'Village executive officer name.';
COMMENT ON COLUMN public.ref_village.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_village.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_village.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_village.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_village.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_village
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_village
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_village
(
  code character varying(20),
  val character varying(500),
  district_code character varying(20),
  address character varying(255),
  chairman character varying(255),
  executive_officer character varying(255),
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
ALTER TABLE history.ref_village OWNER TO postgres;

-- Hamlets

CREATE TABLE public.ref_hamlet
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  abbr character varying(10) NOT NULL,
  village_code character varying(20) NOT NULL,
  leader character varying(255),
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_hamlet_pkey PRIMARY KEY (code),
  CONSTRAINT ref_hamlet_val_unique UNIQUE (village_code, val),
  CONSTRAINT ref_hamlet_abbr_unique UNIQUE (village_code, abbr),
  CONSTRAINT ref_hamlet_ref_village_fk FOREIGN KEY (village_code)
      REFERENCES public.ref_village (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_hamlet OWNER TO postgres;
COMMENT ON TABLE public.ref_hamlet IS 'Contains list of village hamlets';
COMMENT ON COLUMN public.ref_hamlet.code IS 'Hamlet unique code.';
COMMENT ON COLUMN public.ref_hamlet.abbr IS 'Hamlet abbreviation, unique for the village.';
COMMENT ON COLUMN public.ref_hamlet.val IS 'Hamlet name.';
COMMENT ON COLUMN public.ref_hamlet.village_code IS 'Village code.';
COMMENT ON COLUMN public.ref_hamlet.leader IS 'Hamlet leader name.';
COMMENT ON COLUMN public.ref_hamlet.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_hamlet.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_hamlet.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_hamlet.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_hamlet.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_hamlet
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_hamlet
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_hamlet
(
  code character varying(20),
  val character varying(500),
  abbr character varying(10),
  village_code character varying(20),
  leader character varying(255),
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
ALTER TABLE history.ref_hamlet OWNER TO postgres;

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
COMMENT ON TABLE public.ref_doc_type IS 'Contains list of document types';
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

-- Media

CREATE SCHEMA media;

CREATE TABLE media.file
(
   id character varying(40) NOT NULL, 
   file_path character varying(255) NOT NULL, 
   original_file_name character varying(255), 
   media_type character varying(255) NOT NULL, 
   file_size integer NOT NULL DEFAULT 0,
   creation_time timestamp without time zone NOT NULL DEFAULT now(),
   created_by character varying(50) NOT NULL DEFAULT 'db:' || current_user,
   CONSTRAINT file_pk PRIMARY KEY (id)
)
WITH (
  OIDS = FALSE
);
ALTER TABLE media.file OWNER TO postgres;

COMMENT ON COLUMN media.file.id IS 'Row identifier';
COMMENT ON COLUMN media.file.file_path IS 'Physical file path on the hard drive. The path must be relative and will be prepended by the system.';
COMMENT ON COLUMN media.file.original_file_name IS 'Original file name that was uploaded.';
COMMENT ON COLUMN media.file.media_type IS 'Media type of the file, for transmission over the Internet.';
COMMENT ON COLUMN media.file.file_size IS 'File size in bytes.';
COMMENT ON COLUMN media.file.created_by IS 'User name, who created the record.';
COMMENT ON COLUMN media.file.creation_time IS 'Date and time, when the file was inserted.';
COMMENT ON TABLE public.document
  IS 'Contains information on the files, attached to the documents or persons.';

-- Documents

CREATE TABLE public.document
(
   id character varying(40) NOT NULL, 
   type_code character varying(20) NOT NULL, 
   ref_number character varying(20), 
   doc_date date, 
   authority character varying(255), 
   expiry_date date, 
   file_id character varying(40), 
   description character varying(500), 
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(),
   CONSTRAINT document_pk PRIMARY KEY (id), 
   CONSTRAINT document_file_fk FOREIGN KEY (file_id) REFERENCES media.file (id) ON UPDATE NO ACTION ON DELETE NO ACTION,
   CONSTRAINT document_ref_doc_type_fk FOREIGN KEY (type_code) REFERENCES public.ref_doc_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.document OWNER TO postgres;
COMMENT ON COLUMN public.document.id IS 'Row identifier';
COMMENT ON COLUMN public.document.type_code IS 'Document type code';
COMMENT ON COLUMN public.document.ref_number IS 'Document reference number';
COMMENT ON COLUMN public.document.doc_date IS 'Document date';
COMMENT ON COLUMN public.document.authority IS 'Authority name, issued the document';
COMMENT ON COLUMN public.document.expiry_date IS 'Document expiration date as stated by the issuing authrority';
COMMENT ON COLUMN public.document.file_id IS 'File id reference.';
COMMENT ON COLUMN public.document.description IS 'Additional document description';
COMMENT ON COLUMN public.document.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.document.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.document.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.document.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.document
  IS 'Contains various documents, related to application, right or person.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.document
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.document
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();
  
CREATE TABLE history.document
(
   id character varying(40), 
   type_code character varying(20), 
   ref_number character varying(20), 
   doc_date date, 
   authority character varying(255), 
   expiry_date date, 
   file_id character varying(40), 
   description character varying(500), 
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone,
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);

ALTER TABLE history.document OWNER TO postgres;

-- Application

CREATE SEQUENCE public.application_number_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.application_number_seq
  OWNER TO postgres;

CREATE TABLE public.application
(
   id character varying(40) NOT NULL, 
   app_type_code character varying(20) NOT NULL, 
   app_number character varying(20) NOT NULL, 
   lodgement_date timestamp without time zone NOT NULL DEFAULT now(), 
   status_code character varying(20) NOT NULL DEFAULT 'pending', 
   approve_reject_date timestamp without time zone, 
   reject_reason character varying(1000), 
   withdraw_date timestamp without time zone, 
   withdraw_reason character varying(1000), 
   assignee character varying(50) NOT NULL DEFAULT 'db:' || current_user,
   assigned_on timestamp without time zone NOT NULL DEFAULT now(),
   complete_date timestamp without time zone, 
   comment character varying(40), 
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(),
   CONSTRAINT application_pk PRIMARY KEY (id), 
   CONSTRAINT application_app_number_unique UNIQUE (app_number), 
   CONSTRAINT application_ref_app_type_fk FOREIGN KEY (app_type_code) REFERENCES public.ref_app_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT application_ref_app_status FOREIGN KEY (status_code) REFERENCES public.ref_app_status (code) ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.application OWNER TO postgres;
COMMENT ON COLUMN public.application.id IS 'Row identifier';
COMMENT ON COLUMN public.application.app_type_code IS 'Application type code.';
COMMENT ON COLUMN public.application.app_number IS 'Unique application number.';
COMMENT ON COLUMN public.application.lodgement_date IS 'Application lodgement date and time';
COMMENT ON COLUMN public.application.status_code IS 'Status code.';
COMMENT ON COLUMN public.application.approve_reject_date IS 'Approval or rejection date';
COMMENT ON COLUMN public.application.reject_reason IS 'Rejection reason.';
COMMENT ON COLUMN public.application.withdraw_date IS 'Withdrawal date.';
COMMENT ON COLUMN public.application.withdraw_reason IS 'Withdrawal reason.';
COMMENT ON COLUMN public.application.assignee IS 'Assignee user name.';
COMMENT ON COLUMN public.application.assigned_on IS 'Date and time when applicatio was assigned to the user';
COMMENT ON COLUMN public.application.complete_date IS 'Complition date. Can be date of issuance of certificate.';
COMMENT ON COLUMN public.application.comment IS 'Free text comment to the application.';
COMMENT ON COLUMN public.application.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.application.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.application.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.application.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.application
  IS 'Contains applications to execute various transactions';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.application
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.application
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE OR REPLACE FUNCTION public.f_generate_app_number()
  RETURNS trigger AS
$BODY$
DECLARE
 office_code varchar;
BEGIN
    IF (TG_OP = 'INSERT') THEN
        office_code = (select val from public.setting where id = 'office-code' limit 1);
        IF (office_code IS null OR office_code = '') THEN
          office_code = 'APP';
        END IF;
        NEW.app_number = (SELECT upper(office_code) || '/A' || LPAD(nextval('public.application_number_seq')::text, 7, '0'));
    END IF;
    RETURN NEW;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.f_generate_app_number()
  OWNER TO postgres;
COMMENT ON FUNCTION public.f_generate_app_number() IS 'This function is used to generate application number, using office-code system setting and application row_id value.';

CREATE TRIGGER __generate_number
  BEFORE INSERT
  ON public.application
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_generate_app_number();
  
CREATE TABLE history.application
(
   id character varying(40), 
   app_type_code character varying(20), 
   app_number character varying(20), 
   lodgement_date timestamp without time zone, 
   status_code character varying(20), 
   approve_reject_date timestamp without time zone, 
   reject_reason character varying(1000), 
   withdraw_date timestamp without time zone, 
   withdraw_reason character varying(1000), 
   assignee character varying(50), 
   assigned_on timestamp without time zone,
   complete_date timestamp without time zone, 
   comment character varying(40), 
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone,
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.application OWNER TO postgres;

-- Application documents

CREATE TABLE public.application_document
(
   app_id character varying(40) NOT NULL, 
   document_id character varying(40) NOT NULL,
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(),
   CONSTRAINT application_document_pk PRIMARY KEY (app_id, document_id), 
   CONSTRAINT application_document_application_fk FOREIGN KEY (app_id) REFERENCES public.application (id) ON UPDATE CASCADE ON DELETE CASCADE, 
   CONSTRAINT application_document_document_fk FOREIGN KEY (document_id) REFERENCES public.document (id) ON UPDATE CASCADE ON DELETE CASCADE
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.application_document OWNER TO postgres;
COMMENT ON COLUMN public.application_document.app_id IS 'Application identifier.';
COMMENT ON COLUMN public.application_document.document_id IS 'Document identifier.';
COMMENT ON COLUMN public.application_document.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.application_document.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.application_document.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.application_document.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.application_document
  IS 'Contains relations between applications and documents.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.application_document
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.application_document
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.application_document
(
   app_id character varying(40) NOT NULL, 
   document_id character varying(40) NOT NULL,
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone,
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE history.application_document OWNER TO postgres;

-- Entity types

CREATE TABLE public.ref_entity_type
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_entity_type_pkey PRIMARY KEY (code),
  CONSTRAINT ref_entity_type_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_entity_type OWNER TO postgres;
COMMENT ON TABLE public.ref_entity_type IS 'Contains list of legal entity types';
COMMENT ON COLUMN public.ref_entity_type.code IS 'Entity type code.';
COMMENT ON COLUMN public.ref_entity_type.val IS 'Entity type name.';
COMMENT ON COLUMN public.ref_entity_type.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_entity_type.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_entity_type.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_entity_type.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_entity_type.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_entity_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_entity_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_entity_type
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
ALTER TABLE history.ref_entity_type OWNER TO postgres;

-- Genders

CREATE TABLE public.ref_gender
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_gender_pkey PRIMARY KEY (code),
  CONSTRAINT ref_gender_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_gender OWNER TO postgres;
COMMENT ON TABLE public.ref_gender IS 'Contains list of genders';
COMMENT ON COLUMN public.ref_gender.code IS 'Gender code.';
COMMENT ON COLUMN public.ref_gender.val IS 'Gender name.';
COMMENT ON COLUMN public.ref_gender.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_gender.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_gender.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_gender.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_gender.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_gender
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_gender
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_gender
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
ALTER TABLE history.ref_gender OWNER TO postgres;

-- Marital statuses

CREATE TABLE public.ref_marital_status
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_marital_status_pkey PRIMARY KEY (code),
  CONSTRAINT ref_marital_status_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_marital_status OWNER TO postgres;
COMMENT ON TABLE public.ref_marital_status IS 'Contains list of marital statuses';
COMMENT ON COLUMN public.ref_marital_status.code IS 'Marital status code.';
COMMENT ON COLUMN public.ref_marital_status.val IS 'Marital status name.';
COMMENT ON COLUMN public.ref_marital_status.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_marital_status.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_marital_status.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_marital_status.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_marital_status.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_marital_status
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_marital_status
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_marital_status
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
ALTER TABLE history.ref_marital_status OWNER TO postgres;

-- Citizenships

CREATE TABLE public.ref_citizenship
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_citizenship_pkey PRIMARY KEY (code),
  CONSTRAINT ref_citizenship_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_citizenship OWNER TO postgres;
COMMENT ON TABLE public.ref_citizenship IS 'Contains list of citizenships';
COMMENT ON COLUMN public.ref_citizenship.code IS 'CItizenship code.';
COMMENT ON COLUMN public.ref_citizenship.val IS 'Citizenship name.';
COMMENT ON COLUMN public.ref_citizenship.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_citizenship.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_citizenship.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_citizenship.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_citizenship.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_citizenship
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_citizenship
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_citizenship
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
ALTER TABLE history.ref_citizenship OWNER TO postgres;

-- ID types

CREATE TABLE public.ref_id_type
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_id_type_pkey PRIMARY KEY (code),
  CONSTRAINT ref_id_type_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_id_type OWNER TO postgres;
COMMENT ON TABLE public.ref_id_type IS 'Contains list of identification document types';
COMMENT ON COLUMN public.ref_id_type.code IS 'ID type code.';
COMMENT ON COLUMN public.ref_id_type.val IS 'ID type name.';
COMMENT ON COLUMN public.ref_id_type.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_id_type.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_id_type.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_id_type.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_id_type.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_id_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_id_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_id_type
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
ALTER TABLE history.ref_id_type OWNER TO postgres;

-- Party statuses

CREATE TABLE public.ref_party_status
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_party_status_pkey PRIMARY KEY (code),
  CONSTRAINT ref_party_status_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_party_status OWNER TO postgres;
COMMENT ON TABLE public.ref_party_status IS 'Contains list party statuses';
COMMENT ON COLUMN public.ref_party_status.code IS 'Party status code.';
COMMENT ON COLUMN public.ref_party_status.val IS 'Party status name.';
COMMENT ON COLUMN public.ref_party_status.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_party_status.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_party_status.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_party_status.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_party_status.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_party_status
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_party_status
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_party_status
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
ALTER TABLE history.ref_party_status OWNER TO postgres;

-- Party

CREATE TABLE public.party
(
   id character varying(40) NOT NULL, 
   is_private boolean NOT NULL DEFAULT 't',
   name1 character varying(255) NOT NULL, 
   name2 character varying(255), 
   name3 character varying(255), 
   name4 character varying(255), 
   citizenship_code character varying(20), 
   gender_code character varying(20), 
   id_type_code character varying(20), 
   id_number character varying(40), 
   entity_type_code character varying(20), 
   marital_status_code character varying(20), 
   dob date, 
   is_resident boolean NOT NULL DEFAULT 't', 
   mobile_number character varying(40), 
   address character varying(255), 
   person_photo_id character varying(40),
   parent_id character varying(40), 
   application_id character varying(40), 
   end_application_id character varying(40), 
   status_code character varying(20) NOT NULL DEFAULT 'active',
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(), 
   CONSTRAINT party_pk PRIMARY KEY (id), 
   CONSTRAINT party_ref_gender_fk FOREIGN KEY (gender_code) REFERENCES public.ref_gender (code) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT party_ref_citizenship_fk FOREIGN KEY (citizenship_code) REFERENCES public.ref_citizenship (code) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT party_ref_id_type_fk FOREIGN KEY (id_type_code) REFERENCES public.ref_id_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT party_ref_entity_type_fk FOREIGN KEY (entity_type_code) REFERENCES public.ref_entity_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT party_ref_marital_status_fk FOREIGN KEY (marital_status_code) REFERENCES public.ref_marital_status (code) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT party_application_fk FOREIGN KEY (application_id) REFERENCES public.application (id) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT party_application_end_fk FOREIGN KEY (end_application_id) REFERENCES public.application (id) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT party_file_fk FOREIGN KEY (person_photo_id) REFERENCES media.file (id) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT party_party_parent_id_fk FOREIGN KEY (parent_id) REFERENCES public.party (id) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT party_ref_party_status_fk FOREIGN KEY (status_code) REFERENCES public.ref_party_status (code) ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
);

ALTER TABLE public.party OWNER TO postgres;
COMMENT ON COLUMN public.party.id IS 'Record identifier - primary key.';
COMMENT ON COLUMN public.party.is_private IS 'Boolean flag, indicating if party is private person or not. True value means private person.';
COMMENT ON COLUMN public.party.name1 IS 'First name of the party. For legal entities is a name of entity.';
COMMENT ON COLUMN public.party.name2 IS 'Seond name of party. For private person can be last name. For legal entity this field is not used.';
COMMENT ON COLUMN public.party.name3 IS 'Third name of party. For private person can be middle name or other name. For legal entity this field is not used.';
COMMENT ON COLUMN public.party.name4 IS 'Fourth name of party. For private person can be alias. For legal entity this field is not used.';
COMMENT ON COLUMN public.party.gender_code IS 'Gender code. For legal entity this field is not used.';
COMMENT ON COLUMN public.party.citizenship_code IS 'Citizenship code. For legal entity this field is not used.';
COMMENT ON COLUMN public.party.gender_code IS 'Gender code. For legal entity this field is not used.';
COMMENT ON COLUMN public.party.id_type_code IS 'ID type code. For legal entity this field is not used.';
COMMENT ON COLUMN public.party.id_number IS 'ID number. For legal entities it can be tax registration number.';
COMMENT ON COLUMN public.party.entity_type_code IS 'Legal entity type code. This field is not used for private persons.';
COMMENT ON COLUMN public.party.marital_status_code IS 'Marital status code. For legal entity this field is not used.';
COMMENT ON COLUMN public.party.dob IS 'Date of birth for provate person and establishment date for legal entities.';
COMMENT ON COLUMN public.party.is_resident IS 'Boolean flag indicating whether private person is a resident of a village or country. Legal entities are supposed to be resident by default.';
COMMENT ON COLUMN public.party.mobile_number IS 'Mobile phone number(s)';
COMMENT ON COLUMN public.party.address IS 'Person or legal entity address';
COMMENT ON COLUMN public.party.person_photo_id IS 'File id reference, containig path to the person photo. Not applicable for legal entities.';
COMMENT ON COLUMN public.party.parent_id IS 'Parent party id. This field is used to link party history, when modifying through official request.';
COMMENT ON COLUMN public.party.application_id IS 'Application id, used to create the party.';
COMMENT ON COLUMN public.party.end_application_id IS 'Application id, used to make the party historic.';
COMMENT ON COLUMN public.party.status_code IS 'Party status code.';
COMMENT ON COLUMN public.party.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.party.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.party.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.party.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.party
  IS 'Contains private persons and legal entities, used further in applications, rights registration and other cases.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.party
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.party
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();
  
CREATE TABLE history.party
(
   id character varying(40), 
   is_private boolean,
   name1 character varying(255), 
   name2 character varying(255), 
   name3 character varying(255), 
   name4 character varying(255), 
   citizenship_code character varying(20), 
   gender_code character varying(20), 
   id_type_code character varying(20), 
   id_number character varying(40), 
   entity_type_code character varying(20), 
   marital_status_code character varying(20), 
   dob date, 
   is_resident boolean, 
   mobile_number character varying(40), 
   address character varying(255), 
   person_photo_id character varying(40),
   parent_id character varying(40), 
   application_id character varying(40), 
   end_application_id character varying(40), 
   status_code character varying(20),
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone, 
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);

ALTER TABLE history.party OWNER TO postgres;

-- Party documents

CREATE TABLE public.party_document
(
   party_id character varying(40) NOT NULL, 
   document_id character varying(40) NOT NULL,
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(),
   CONSTRAINT party_document_pk PRIMARY KEY (party_id, document_id), 
   CONSTRAINT party_document_application_fk FOREIGN KEY (party_id) REFERENCES public.party (id) ON UPDATE CASCADE ON DELETE CASCADE, 
   CONSTRAINT party_document_document_fk FOREIGN KEY (document_id) REFERENCES public.document (id) ON UPDATE CASCADE ON DELETE CASCADE
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.party_document OWNER TO postgres;
COMMENT ON COLUMN public.party_document.party_id IS 'Party identifier.';
COMMENT ON COLUMN public.party_document.document_id IS 'Document identifier.';
COMMENT ON COLUMN public.party_document.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.party_document.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.party_document.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.party_document.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.party_document
  IS 'Contains relations between parties and documents.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.party_document
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.party_document
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.party_document
(
   party_id character varying(40) NOT NULL, 
   document_id character varying(40) NOT NULL,
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone,
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE history.party_document OWNER TO postgres;

-- Applicants

CREATE TABLE public.application_party
(
   app_id character varying(40) NOT NULL, 
   party_id character varying(40) NOT NULL,
   is_contact boolean NOT NULL DEFAULT 'f',
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(),
   CONSTRAINT application_party_pk PRIMARY KEY (app_id, party_id), 
   CONSTRAINT application_party_application_fk FOREIGN KEY (app_id) REFERENCES public.application (id) ON UPDATE CASCADE ON DELETE CASCADE, 
   CONSTRAINT application_party_document_fk FOREIGN KEY (party_id) REFERENCES public.party (id) ON UPDATE CASCADE ON DELETE CASCADE
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.application_party OWNER TO postgres;
COMMENT ON COLUMN public.application_party.app_id IS 'Application identifier.';
COMMENT ON COLUMN public.application_party.party_id IS 'Party identifier.';
COMMENT ON COLUMN public.application_party.is_contact IS 'Indicates whether this party is a contact person for application.';
COMMENT ON COLUMN public.application_party.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.application_party.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.application_party.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.application_party.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.application_party
  IS 'Contains relations between applications and parties as applicants.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.application_party
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.application_party
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.application_party
(
   app_id character varying(40) NOT NULL, 
   party_id character varying(40) NOT NULL,
   is_contact boolean,
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone,
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE history.application_party OWNER TO postgres;

-- Party to change

CREATE TABLE public.application_party_for_change
(
   app_id character varying(40) NOT NULL, 
   party_id character varying(40) NOT NULL,
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(),
   CONSTRAINT application_party_for_change_pk PRIMARY KEY (app_id, party_id), 
   CONSTRAINT application_party_for_change_application_fk FOREIGN KEY (app_id) REFERENCES public.application (id) ON UPDATE CASCADE ON DELETE CASCADE, 
   CONSTRAINT application_party_for_change_document_fk FOREIGN KEY (party_id) REFERENCES public.party (id) ON UPDATE CASCADE ON DELETE CASCADE
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.application_party_for_change OWNER TO postgres;
COMMENT ON COLUMN public.application_party_for_change.app_id IS 'Application identifier.';
COMMENT ON COLUMN public.application_party_for_change.party_id IS 'Party identifier.';
COMMENT ON COLUMN public.application_party_for_change.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.application_party_for_change.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.application_party_for_change.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.application_party_for_change.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.application_party_for_change
  IS 'Contains parties which are requested for change of details through application.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.application_party_for_change
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.application_party_for_change
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.application_party_for_change
(
   app_id character varying(40) NOT NULL, 
   party_id character varying(40) NOT NULL,
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone,
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE history.application_party_for_change OWNER TO postgres;

-- Land type

CREATE TABLE public.ref_land_type
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_land_type_pkey PRIMARY KEY (code),
  CONSTRAINT ref_land_type_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_land_type OWNER TO postgres;
COMMENT ON TABLE public.ref_land_type IS 'Contains list of land types';
COMMENT ON COLUMN public.ref_land_type.code IS 'Land type code.';
COMMENT ON COLUMN public.ref_land_type.val IS 'Land type name.';
COMMENT ON COLUMN public.ref_land_type.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_land_type.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_land_type.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_land_type.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_land_type.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_land_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_land_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_land_type
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
ALTER TABLE history.ref_land_type OWNER TO postgres;

-- Parcel status

CREATE TABLE public.ref_parcel_status
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_parcel_status_pkey PRIMARY KEY (code),
  CONSTRAINT ref_parcel_status_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_parcel_status OWNER TO postgres;
COMMENT ON TABLE public.ref_parcel_status IS 'Contains list of parcel statuses';
COMMENT ON COLUMN public.ref_parcel_status.code IS 'Parcel status code.';
COMMENT ON COLUMN public.ref_parcel_status.val IS 'Parcel status name.';
COMMENT ON COLUMN public.ref_parcel_status.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_parcel_status.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_parcel_status.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_parcel_status.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_parcel_status.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_parcel_status
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_parcel_status
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_parcel_status
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
ALTER TABLE history.ref_parcel_status OWNER TO postgres;

-- Parcels

CREATE TABLE public.parcel
(
   id character varying(40) NOT NULL DEFAULT uuid_generate_v4(), 
   land_type_code character varying(20), 
   uka character varying(20) NOT NULL, 
   survey_date date NOT NULL DEFAULT now(), 
   hamlet_code character varying(20) NOT NULL, 
   address character varying(255), 
   geom geometry NOT NULL, 
   comment character varying(500), 
   application_id character varying(40), 
   end_application_id character varying(40),
   status_code character varying(20) NOT NULL DEFAULT 'active',
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(), 
   CONSTRAINT parcel_pk PRIMARY KEY (id), 
   CONSTRAINT parcel_uka_unique UNIQUE (uka),
   CONSTRAINT parcel_ref_land_type_fk FOREIGN KEY (land_type_code) REFERENCES public.ref_land_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT parcel_ref_hamlet_fk FOREIGN KEY (hamlet_code) REFERENCES public.ref_hamlet (code) ON UPDATE NO ACTION ON DELETE NO ACTION,
   CONSTRAINT parcel_application_fk FOREIGN KEY (application_id) REFERENCES public.application (id) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT parcel_application_end_fk FOREIGN KEY (end_application_id) REFERENCES public.application (id) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT parcel_ref_parcel_status_fk FOREIGN KEY (status_code) REFERENCES public.ref_parcel_status (code) ON UPDATE NO ACTION ON DELETE NO ACTION,
   CONSTRAINT parcel_geom_type_check CHECK (geometrytype(geom) = 'POLYGON'::text),
   CONSTRAINT parcel_geom_srid_check CHECK (st_srid(geom) = 4326),
   CONSTRAINT parcel_valid_geom_check CHECK (st_isvalid(geom))
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.parcel OWNER TO postgres;
COMMENT ON COLUMN public.parcel.id IS 'Record identifier.';
COMMENT ON COLUMN public.parcel.land_type_code IS 'Land type code.';
COMMENT ON COLUMN public.parcel.uka IS 'Unique parcel identifier.';
COMMENT ON COLUMN public.parcel.survey_date IS 'Parcel surveying date.';
COMMENT ON COLUMN public.parcel.hamlet_code IS 'Village hamlet code.';
COMMENT ON COLUMN public.parcel.address IS 'Physical address of the land parcel.';
COMMENT ON COLUMN public.parcel.geom IS 'Land parcel geometry.';
COMMENT ON COLUMN public.parcel.comment IS 'Free text comments.';
COMMENT ON COLUMN public.parcel.application_id IS 'Application id, used to create the party.';
COMMENT ON COLUMN public.parcel.end_application_id IS 'Application id, used to make the party historic.';
COMMENT ON COLUMN public.parcel.status_code IS 'Parcel status code.';
COMMENT ON COLUMN public.parcel.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.parcel.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.parcel.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.parcel.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.parcel
  IS 'Contains land parcels.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.parcel
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.parcel
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE OR REPLACE FUNCTION public.f_generate_uka()
  RETURNS trigger AS
$BODY$
DECLARE
 vl_code varchar;
 hamlet_abbr varchar;
 next_number integer;
BEGIN
    IF (TG_OP = 'INSERT' AND (NEW.uka IS null OR NEW.uka = '')) THEN
        vl_code = (select upper(village_code) from public.ref_hamlet where code = NEW.hamlet_code limit 1);
        hamlet_abbr = (select upper(abbr) from public.ref_hamlet where code = NEW.hamlet_code limit 1);
	next_number = (SELECT MAX(case when right(COALESCE(uka,''), position('/' in reverse(COALESCE(uka,'')))-1) ~ '^[0-9]+$' then (right(COALESCE(uka,''), position('/' in reverse(COALESCE(uka,'')))-1))::integer else 0 end) + 1 
			FROM public.parcel WHERE hamlet_code IN (SELECT code FROM ref_hamlet WHERE village_code = vl_code)); 
        IF (next_number IS null OR next_number = 0) THEN
	  next_number = 1;
        END IF;
        NEW.uka = (SELECT vl_code || '/' || hamlet_abbr || '/' || next_number::text);
    ELSE
	NEW.uka = upper(NEW.uka);
    END IF;
    RETURN NEW;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.f_generate_app_number()
  OWNER TO postgres;
COMMENT ON FUNCTION public.f_generate_app_number() IS 'This function is used to generate land parcel uka number based on hamlet and village code.';

CREATE TRIGGER __generate_uka
  BEFORE INSERT
  ON public.parcel
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_generate_uka();

CREATE TABLE history.parcel
(
   id character varying(40), 
   land_type_code character varying(20), 
   uka character varying(20), 
   survey_date date, 
   hamlet_code character varying(20), 
   address character varying(255), 
   geom geometry, 
   comment character varying(500), 
   application_id character varying(40), 
   end_application_id character varying(40),
   status_code character varying(20),
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone, 
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);

ALTER TABLE history.parcel OWNER TO postgres;

-- Occupancy types

CREATE TABLE public.ref_occupancy_type
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_occupancy_type_pkey PRIMARY KEY (code),
  CONSTRAINT ref_occupancy_type_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_occupancy_type OWNER TO postgres;
COMMENT ON TABLE public.ref_occupancy_type IS 'Contains list of occupancy types';
COMMENT ON COLUMN public.ref_occupancy_type.code IS 'Occupancy type code.';
COMMENT ON COLUMN public.ref_occupancy_type.val IS 'Occupancy type name.';
COMMENT ON COLUMN public.ref_occupancy_type.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_occupancy_type.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_occupancy_type.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_occupancy_type.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_occupancy_type.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_occupancy_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_occupancy_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_occupancy_type
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
ALTER TABLE history.ref_occupancy_type OWNER TO postgres;

-- Land uses

CREATE TABLE public.ref_landuse
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_landuse_pkey PRIMARY KEY (code),
  CONSTRAINT ref_landuse_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_landuse OWNER TO postgres;
COMMENT ON TABLE public.ref_landuse IS 'Contains list of land uses';
COMMENT ON COLUMN public.ref_landuse.code IS 'Land use code.';
COMMENT ON COLUMN public.ref_landuse.val IS 'Land use name.';
COMMENT ON COLUMN public.ref_landuse.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_landuse.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_landuse.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_landuse.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_landuse.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_landuse
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_landuse
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_landuse
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
ALTER TABLE history.ref_landuse OWNER TO postgres;

-- Property

CREATE TABLE public.property
(
   id character varying(40) NOT NULL, 
   parcel_id character varying NOT NULL, 
   reg_date date, 
   file_number character varying(20), 
   prop_number character varying(20) NOT NULL, 
   termination_date date, 
   application_id character varying(40) NOT NULL, 
   end_application_id character varying(40), 
   status_code character varying(20) NOT NULL DEFAULT 'pending', 
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(), 
   CONSTRAINT property_pk PRIMARY KEY (id), 
   CONSTRAINT property_parcel_fk FOREIGN KEY (parcel_id) REFERENCES public.parcel (id) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT prop_number_unique UNIQUE (prop_number),
   CONSTRAINT property_application_fk FOREIGN KEY (application_id) REFERENCES public.application (id) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT property_application_end_fk FOREIGN KEY (end_application_id) REFERENCES public.application (id) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT property_ref_reg_status_fk FOREIGN KEY (status_code) REFERENCES public.ref_reg_status (code) ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.property OWNER TO postgres;
COMMENT ON COLUMN public.property.id IS 'Record identifier.';
COMMENT ON COLUMN public.property.parcel_id IS 'Parcel id.';
COMMENT ON COLUMN public.property.reg_date IS 'Registaration/approval date of the property.';
COMMENT ON COLUMN public.property.file_number IS 'File number in the paper archive.';
COMMENT ON COLUMN public.property.prop_number IS 'Unique property number, which is CCRO number.';
COMMENT ON COLUMN public.property.termination_date IS 'Termination or surrender date of the property.';
COMMENT ON COLUMN public.property.application_id IS 'Application id, used to create this property.';
COMMENT ON COLUMN public.property.end_application_id IS 'Application id, used to terminate/surrender this property.';
COMMENT ON COLUMN public.property.status_code IS 'Registration status code.';
COMMENT ON COLUMN public.property.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.property.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.property.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.property.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.property
  IS 'Main container, linking together land parcel and differnt rights.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.property
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.property
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE OR REPLACE FUNCTION public.f_prop_number()
  RETURNS trigger AS
$BODY$
DECLARE
 vl_code varchar;
 next_number integer;
BEGIN
    IF (TG_OP = 'INSERT' AND (NEW.prop_number IS null OR NEW.prop_number = '')) THEN
        vl_code = (select upper(h.village_code) from public.ref_hamlet h inner join public.parcel p on h.code = p.hamlet_code where p.id = NEW.parcel_id limit 1);
	next_number = (SELECT MAX(case when right(COALESCE(prop_number,''), position('/' in reverse(COALESCE(prop_number,'')))-1) ~ '^[0-9]+$' then (right(COALESCE(prop_number,''), position('/' in reverse(COALESCE(prop_number,'')))-1))::integer else 0 end) + 1 
			FROM public.property WHERE position(upper(vl_code || '/') in prop_number) > 0);
			
        IF (next_number IS null OR next_number = 0) THEN
	  next_number = 20001;
        END IF;
        NEW.prop_number = (SELECT vl_code || '/' || next_number::text);
    ELSE
	NEW.prop_number = upper(NEW.prop_number);
    END IF;
    RETURN NEW;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.f_prop_number()
  OWNER TO postgres;
COMMENT ON FUNCTION public.f_prop_number() IS 'This function is used to generate unique property number based on attached parcel village code.';

CREATE TRIGGER __generate_prop_number
  BEFORE INSERT
  ON public.property
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_prop_number();

CREATE SEQUENCE public.file_number_seq
  INCREMENT 1
  MINVALUE 60000
  MAXVALUE 999999999999
  START 60000
  CACHE 1;
ALTER TABLE public.file_number_seq
  OWNER TO postgres;
  
CREATE OR REPLACE FUNCTION public.f_file_number()
  RETURNS trigger AS
$BODY$
DECLARE
 next_number integer;
 office_code varchar;
BEGIN
    IF (TG_OP = 'UPDATE' AND (NEW.status_code = 'current'  AND OLD.status_code = 'pending')) THEN
        SELECT nextval('file_number_seq') INTO next_number;
	office_code = (select val from public.setting where id = 'office-code' limit 1);
	NEW.file_number = (upper(COALESCE(office_code, '')) || 'HW/' || next_number::text);
    END IF;
    RETURN NEW;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.f_file_number()
  OWNER TO postgres;
COMMENT ON FUNCTION public.f_file_number() IS 'This function is used to generate archive file number.';

CREATE TRIGGER __generate_file_number
  BEFORE UPDATE
  ON public.property
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_file_number();
  
CREATE TABLE history.property
(
   id character varying(40), 
   parcel_id character varying, 
   reg_date date, 
   file_number character varying(20), 
   prop_number character varying(20), 
   termination_date date, 
   application_id character varying(40), 
   end_application_id character varying(40), 
   status_code character varying(20), 
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone, 
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE history.property OWNER TO postgres;

-- Application property

CREATE TABLE public.application_property
(
   app_id character varying(40) NOT NULL, 
   property_id character varying(40) NOT NULL,
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(),
   CONSTRAINT application_property_pk PRIMARY KEY (app_id, property_id), 
   CONSTRAINT application_property_app_fk FOREIGN KEY (app_id) REFERENCES public.application (id) ON UPDATE CASCADE ON DELETE CASCADE, 
   CONSTRAINT application_property_property_fk FOREIGN KEY (property_id) REFERENCES public.property (id) ON UPDATE CASCADE ON DELETE CASCADE
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.application_property OWNER TO postgres;
COMMENT ON COLUMN public.application_property.app_id IS 'Application identifier.';
COMMENT ON COLUMN public.application_property.property_id IS 'Property identifier.';
COMMENT ON COLUMN public.application_property.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.application_property.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.application_property.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.application_property.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.application_property
  IS 'Contains relations between applications and properties. Used in transfers, split or merge transactions';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.application_property
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.application_property
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.application_property
(
   app_id character varying(40) NOT NULL, 
   property_id character varying(40) NOT NULL,
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone,
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE history.application_property OWNER TO postgres;

-- Rights

CREATE TABLE public.rrr
(
   id character varying(40) NOT NULL, 
   property_id character varying(40),
   parent_id character varying(40), 
   right_type_code character varying(20) NOT NULL, 
   occupancy_type_code character varying(20), 
   allocation_date date, 
   reg_date date, 
   start_date date, 
   end_date date, 
   duration integer, 
   annual_fee double precision, 
   juridical_area double precision, 
   folio_number character varying(20), 
   deal_amount double precision, 
   intereset_rate double precision, 
   declared_landuse_code character varying(20), 
   approved_landuse_code character varying(20), 
   neighbor_north character varying(500), 
   neighbor_south character varying(500), 
   neighbor_east character varying(500), 
   neighbor_west character varying(500), 
   adjudicator1 character varying(250), 
   adjudicator2 character varying(250), 
   description character varying(1000), 
   application_id character varying(40) NOT NULL, 
   end_application_id character varying(40), 
   termination_date date, 
   status_code character varying(20) NOT NULL DEFAULT 'pending', 
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(), 
   CONSTRAINT rrr_pk PRIMARY KEY (id), 
   CONSTRAINT rrr_property_id_fk FOREIGN KEY (property_id) REFERENCES public.property (id) ON UPDATE CASCADE ON DELETE CASCADE, 
   CONSTRAINT rrr_parent_id_fk FOREIGN KEY (parent_id) REFERENCES public.rrr (id) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT rrr_ref_right_type_fk FOREIGN KEY (right_type_code) REFERENCES public.ref_right_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT rrr_ref_occupancy_type_fk FOREIGN KEY (occupancy_type_code) REFERENCES public.ref_occupancy_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT rrr_ref_landuse_declared_fk FOREIGN KEY (declared_landuse_code) REFERENCES public.ref_landuse (code) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT rrr_ref_landuse_approved_fk FOREIGN KEY (approved_landuse_code) REFERENCES public.ref_landuse (code) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT rrr_application_fk FOREIGN KEY (application_id) REFERENCES public.application (id) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT rrr_end_application_fk FOREIGN KEY (end_application_id) REFERENCES public.application (id) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   CONSTRAINT rrr_ref_reg_status_fk FOREIGN KEY (status_code) REFERENCES public.ref_reg_status (code) ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.rrr OWNER TO postgres;
COMMENT ON COLUMN public.rrr.id IS 'Record identifier.';
COMMENT ON COLUMN public.rrr.property_id IS 'Property identifier.';
COMMENT ON COLUMN public.rrr.parent_id IS 'Links current right with its pending modified version. Upon approval, current right will be made historic and pending changed to current.';
COMMENT ON COLUMN public.rrr.right_type_code IS 'Right type code.';
COMMENT ON COLUMN public.rrr.occupancy_type_code IS 'Occupancy type code. Used for ownership rights only.';
COMMENT ON COLUMN public.rrr.allocation_date IS 'Allocation date, when ownerhip right was approved by village counsel.';
COMMENT ON COLUMN public.rrr.reg_date IS 'Date when the right was registered (apporved).';
COMMENT ON COLUMN public.rrr.start_date IS 'Date when the right has commenced.';
COMMENT ON COLUMN public.rrr.end_date IS 'Date when the right expires.';
COMMENT ON COLUMN public.rrr.duration IS 'Duration in years, for how long the right is valid. Can be used instead of end date.';
COMMENT ON COLUMN public.rrr.annual_fee IS 'Annual fee for the land. Applicable only to ownership rights.';
COMMENT ON COLUMN public.rrr.juridical_area IS 'Juridical area of the parcel, which will appear on the certificate. Applicable only for ownership rights.';
COMMENT ON COLUMN public.rrr.folio_number IS 'Folio number, assigned by the registration office.';
COMMENT ON COLUMN public.rrr.deal_amount IS 'Deal amount, when transferring property from one person to another.';
COMMENT ON COLUMN public.rrr.intereset_rate IS 'Interest rate. Applicable for mortgages and other loans.';
COMMENT ON COLUMN public.rrr.declared_landuse_code IS 'Declared land use code of the parcel. Applicable for ownership rights.';
COMMENT ON COLUMN public.rrr.approved_landuse_code IS 'Approved land use code. Applicable for ownership rights.';
COMMENT ON COLUMN public.rrr.neighbor_north IS 'Name of the owner of the parcel on the north side.';
COMMENT ON COLUMN public.rrr.neighbor_south IS 'Name of the owner of the parcel on the south side.';
COMMENT ON COLUMN public.rrr.neighbor_east IS 'Name of the owner of the parcel on the east side.';
COMMENT ON COLUMN public.rrr.neighbor_west IS 'Name of the owner of the parcel on the west side.';
COMMENT ON COLUMN public.rrr.adjudicator1 IS 'Name of the first adjudicator. Applicable only for ownership rights.';
COMMENT ON COLUMN public.rrr.adjudicator2 IS 'Name of the second adjudicator. Applicable only for ownership rights.';
COMMENT ON COLUMN public.rrr.description IS 'Free text description of the right. Can be comments as well.';
COMMENT ON COLUMN public.rrr.application_id IS 'Application id, used to create the right.';
COMMENT ON COLUMN public.rrr.end_application_id IS 'Application id, used to terminate the right.';
COMMENT ON COLUMN public.rrr.termination_date IS 'Right termination date.';
COMMENT ON COLUMN public.rrr.status_code IS 'Registration status code.';
COMMENT ON COLUMN public.rrr.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.rrr.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.rrr.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.rrr.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.rrr
  IS 'Contains ownership rights, restrictions and other rights on the land parcel.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.rrr
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.rrr
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();
  
CREATE TABLE history.rrr
(
   id character varying(40), 
   property_id character varying(40), 
   parent_id character varying(40), 
   right_type_code character varying(20), 
   occupancy_type_code character varying(20), 
   allocation_date date, 
   reg_date date, 
   start_date date, 
   end_date date, 
   duration integer, 
   annual_fee double precision, 
   juridical_area double precision, 
   folio_number character varying(20), 
   deal_amount double precision, 
   intereset_rate double precision, 
   declared_landuse_code character varying(20), 
   approved_landuse_code character varying(20), 
   neighbor_north character varying(500), 
   neighbor_south character varying(500), 
   neighbor_east character varying(500), 
   neighbor_west character varying(500), 
   adjudicator1 character varying(250), 
   adjudicator2 character varying(250), 
   description character varying(1000), 
   application_id character varying(40), 
   end_application_id character varying(40), 
   termination_date date, 
   status_code character varying(20), 
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone,
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE history.rrr OWNER TO postgres;

-- RRR documents

CREATE TABLE public.rrr_document
(
   rrr_id character varying(40) NOT NULL, 
   document_id character varying(40) NOT NULL,
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(),
   CONSTRAINT rrr_document_pk PRIMARY KEY (rrr_id, document_id), 
   CONSTRAINT rrr_document_rrr_fk FOREIGN KEY (rrr_id) REFERENCES public.rrr (id) ON UPDATE CASCADE ON DELETE CASCADE, 
   CONSTRAINT rrr_document_document_fk FOREIGN KEY (document_id) REFERENCES public.document (id) ON UPDATE CASCADE ON DELETE CASCADE
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.rrr_document OWNER TO postgres;
COMMENT ON COLUMN public.rrr_document.rrr_id IS 'RRR identifier.';
COMMENT ON COLUMN public.rrr_document.document_id IS 'Document identifier.';
COMMENT ON COLUMN public.rrr_document.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.rrr_document.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.rrr_document.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.rrr_document.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.rrr_document
  IS 'Contains relations between RRR and documents.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.rrr_document
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.rrr_document
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.rrr_document
(
   rrr_id character varying(40) NOT NULL, 
   document_id character varying(40) NOT NULL,
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone,
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE history.rrr_document OWNER TO postgres;

-- Owner types

CREATE TABLE public.ref_owner_type
(
  code character varying(20) NOT NULL,
  val character varying(500) NOT NULL,
  active boolean NOT NULL DEFAULT TRUE,
  rowversion integer NOT NULL DEFAULT 0,
  action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
  action_user character varying(50),
  action_time timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT ref_owner_type_pkey PRIMARY KEY (code),
  CONSTRAINT ref_owner_type_val_unique UNIQUE (val)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.ref_owner_type OWNER TO postgres;
COMMENT ON TABLE public.ref_owner_type IS 'Contains list of owner types';
COMMENT ON COLUMN public.ref_owner_type.code IS 'Owner type code.';
COMMENT ON COLUMN public.ref_owner_type.val IS 'Owner type name.';
COMMENT ON COLUMN public.ref_owner_type.active IS 'Boolean flag indicating if record is active or not.';
COMMENT ON COLUMN public.ref_owner_type.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.ref_owner_type.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.ref_owner_type.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.ref_owner_type.action_time IS 'Date and time, of the action.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.ref_owner_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.ref_owner_type
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.ref_owner_type
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
ALTER TABLE history.ref_owner_type OWNER TO postgres;

-- Rightholders

CREATE TABLE public.rightholder
(
   rrr_id character varying(40) NOT NULL, 
   party_id character varying(40) NOT NULL, 
   owner_type_code character varying(20), 
   share_size double precision, 
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(),
   CONSTRAINT rightholder_pk PRIMARY KEY (rrr_id, party_id), 
   CONSTRAINT rightholder_rrr_fk FOREIGN KEY (rrr_id) REFERENCES public.rrr (id) ON UPDATE CASCADE ON DELETE CASCADE, 
   CONSTRAINT rightholder_party_fk FOREIGN KEY (party_id) REFERENCES public.party (id) ON UPDATE CASCADE ON DELETE CASCADE, 
   CONSTRAINT rightholder_ref_owner_type_fk FOREIGN KEY (owner_type_code) REFERENCES public.ref_owner_type (code) ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.rightholder OWNER TO postgres;
COMMENT ON COLUMN public.rightholder.rrr_id IS 'Right id.';
COMMENT ON COLUMN public.rightholder.party_id IS 'Party id.';
COMMENT ON COLUMN public.rightholder.owner_type_code IS 'Owner type code. Used for ownership rights.';
COMMENT ON COLUMN public.rightholder.share_size IS 'Share size of the rightholder. Mainly used for ownership rights.';
COMMENT ON COLUMN public.rightholder.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.rightholder.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.rightholder.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.rightholder.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.rightholder
  IS 'Contains rightholders of different rights (ownership, restriction).';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.rightholder
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.rightholder
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();
  
CREATE TABLE history.rightholder
(
   rrr_id character varying(40), 
   party_id character varying(40), 
   owner_type_code character varying(20), 
   share_size double precision, 
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone,
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE history.rightholder OWNER TO postgres;

-- Function to check if party is editable

CREATE OR REPLACE FUNCTION public.check_party_editable(p_id character varying)
  RETURNS boolean AS
$BODY$
BEGIN
  return (select 
		(select count(1) < 1 from rrr r inner join rightholder rh on r.id = rh.rrr_id
		where rh.party_id = p_id and r.status_code != 'pending')
		and
		(select count(1) < 1 from (application a inner join application_party ap on a.id = ap.app_id) inner join party p on ap.party_id = p.id
		where p.id = p_id and a.status_code != 'pending')
		and
		(select count(1) < 1 from (application a inner join application_party_for_change apc on a.id = apc.app_id) inner join party p on apc.party_id = p.id
		where p.id = p_id and a.status_code != 'pending')
	);
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.check_party_editable(character varying)
  OWNER TO postgres;
COMMENT ON FUNCTION public.check_party_editable(character varying) IS 'This function checks whether party can be edited.';

-- POIs

CREATE TABLE public.poi
(
   id character varying(40) NOT NULL, 
   rrr_id character varying(40) NOT NULL, 
   first_name character varying(250) NOT NULL, 
   last_name character varying(250), 
   middle_name character varying(250), 
   dob date, 
   description character varying(1000), 
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(),
   CONSTRAINT poi_pk PRIMARY KEY (id), 
   CONSTRAINT poi_rrr_fk FOREIGN KEY (rrr_id) REFERENCES public.rrr (id) ON UPDATE CASCADE ON DELETE CASCADE
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.poi OWNER TO postgres;
COMMENT ON COLUMN public.poi.id IS 'Record identifier.';
COMMENT ON COLUMN public.poi.rrr_id IS 'Right id.';
COMMENT ON COLUMN public.poi.first_name IS 'First name.';
COMMENT ON COLUMN public.poi.last_name IS 'Last name.';
COMMENT ON COLUMN public.poi.middle_name IS 'Middle name.';
COMMENT ON COLUMN public.poi.dob IS 'Date of birth.';
COMMENT ON COLUMN public.poi.description IS 'Free text description of person of interest.';
COMMENT ON COLUMN public.poi.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.poi.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.poi.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.poi.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.poi
  IS 'List of person of interests on the land parcel.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.poi
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.poi
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.poi
(
   id character varying(40), 
   rrr_id character varying(40), 
   first_name character varying(250), 
   last_name character varying(250), 
   middle_name character varying(250), 
   dob date, 
   description character varying(1000), 
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone,
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.poi OWNER TO postgres;

-- Deceased person

CREATE TABLE public.deceased_owner
(
   id character varying(40) NOT NULL, 
   rrr_id character varying(40) NOT NULL, 
   first_name character varying(250) NOT NULL, 
   last_name character varying(250), 
   middle_name character varying(250), 
   description character varying(1000), 
   rowversion integer NOT NULL DEFAULT 0,
   action_code character(1) NOT NULL DEFAULT 'i'::bpchar,
   action_user character varying(50),
   action_time timestamp without time zone NOT NULL DEFAULT now(),
   CONSTRAINT deceased_owner_pk PRIMARY KEY (id), 
   CONSTRAINT deceased_owner_rrr_fk FOREIGN KEY (rrr_id) REFERENCES public.rrr (id) ON UPDATE CASCADE ON DELETE CASCADE
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.deceased_owner OWNER TO postgres;
COMMENT ON COLUMN public.deceased_owner.id IS 'Record identifier.';
COMMENT ON COLUMN public.deceased_owner.rrr_id IS 'Right id.';
COMMENT ON COLUMN public.deceased_owner.first_name IS 'First name.';
COMMENT ON COLUMN public.deceased_owner.last_name IS 'Last name.';
COMMENT ON COLUMN public.deceased_owner.middle_name IS 'Middle name.';
COMMENT ON COLUMN public.deceased_owner.description IS 'Free text description of person of interest.';
COMMENT ON COLUMN public.deceased_owner.rowversion IS 'Row version number, indicating number of modifications done to the record and controlling concurrent access for modification.';
COMMENT ON COLUMN public.deceased_owner.action_code IS 'Code of action, made to the record. Insert (i), update (u) or delete (d).';
COMMENT ON COLUMN public.deceased_owner.action_user IS 'User name, who created, modified or deleted the record.';
COMMENT ON COLUMN public.deceased_owner.action_time IS 'Date and time, of the action.';
COMMENT ON TABLE public.deceased_owner
  IS 'List of person of interests on the land parcel.';

CREATE TRIGGER __control_changes
  BEFORE INSERT OR UPDATE
  ON public.deceased_owner
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_control_changes();

CREATE TRIGGER __track_history
  AFTER UPDATE OR DELETE
  ON public.deceased_owner
  FOR EACH ROW
  EXECUTE PROCEDURE public.f_for_trg_track_history();

CREATE TABLE history.deceased_owner
(
   id character varying(40), 
   rrr_id character varying(40), 
   first_name character varying(250), 
   last_name character varying(250), 
   middle_name character varying(250), 
   description character varying(1000), 
   rowversion integer,
   action_code character(1),
   action_user character varying(50),
   action_time timestamp without time zone,
   recording_time timestamp without time zone NOT NULL DEFAULT now()
) 
WITH (
  OIDS = FALSE
);
ALTER TABLE public.deceased_owner OWNER TO postgres;

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
COMMENT ON TABLE public.approle IS 'Contains list of application security roles used to define access to different parts of the application';
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
COMMENT ON TABLE public.appgroup IS 'Contains list of groups for grouping application users.';
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
 SELECT DISTINCT u.username, rg.role_code AS rolename
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

-- Add initial data

-- Roles
INSERT INTO public.approle(code, role_name, description) VALUES ('Admin', 'Administration', 'Used for managing users, groups and system settings.');
INSERT INTO public.approle(code, role_name, description) VALUES ('ManageRefData', 'Reference data management', 'Used for managing reference data tables.');
INSERT INTO public.approle(code, role_name, description) VALUES ('ManageParcels', 'Parcel management', 'Used for creation and modification of parcels on the map.');
INSERT INTO public.approle(code, role_name, description) VALUES ('ManageApps', 'Applications management', 'Used for creation and modification of applications.');
INSERT INTO public.approle(code, role_name, description) VALUES ('WithdrawApps', 'Applications withdrawal', 'Used to withdraw applications.');
INSERT INTO public.approle(code, role_name, description) VALUES ('AssignApps', 'Applications assignment', 'Used to assign applications to other users from current assignee.');
INSERT INTO public.approle(code, role_name, description) VALUES ('ReAssignApps', 'Applications re-assignment', 'Used to re-assign applications from any user, who is not current assignee.');
INSERT INTO public.approle(code, role_name, description) VALUES ('Viewing', 'Viewing information', 'Used for viewing different information in the system (application, parcel, person, etc.).');
INSERT INTO public.approle(code, role_name, description) VALUES ('ManageRights', 'Management of property rights', 'Used to manage various property rights. Allows new registration, transfer, surrender, discharge, etc.');
INSERT INTO public.approle(code, role_name, description) VALUES ('ApproveTrans', 'Approval/Rejection of transactions', 'Used to approve or reject transactions.');
INSERT INTO public.approle(code, role_name, description) VALUES ('GenerateTitle', 'Generate CCRO', 'Used to generate CCRO.');
INSERT INTO public.approle(code, role_name, description) VALUES ('ManageOwners', 'Right holders management', 'Used to create and modify personal information of right holders.');
INSERT INTO public.approle(code, role_name, description) VALUES ('Search', 'Searching', 'Used to search various system object (applications, persons, parcels, etc.).');
INSERT INTO public.approle(code, role_name, description) VALUES ('ViewReports', 'Reports viewing', 'Used to generate and view reports.');

-- Groups
INSERT INTO public.appgroup(id, group_name, description) VALUES ('admin-group', 'Administrators', 'Group of administrators');
INSERT INTO public.appgroup(id, group_name, description) VALUES ('chief-registrar-group', 'Chief Registrars', 'Group of chief/senior registrars');
INSERT INTO public.appgroup(id, group_name, description) VALUES ('front-office-group', 'Front Office', 'Group of front offices officiers');
INSERT INTO public.appgroup(id, group_name, description) VALUES ('registrar-group', 'Registrars', 'Group of registrars');
INSERT INTO public.appgroup(id, group_name, description) VALUES ('surveyor-group', 'Surveyors', 'Group of surveyors');
INSERT INTO public.appgroup(id, group_name, description) VALUES ('ceo-group', 'CEO', 'Group of office executives');

-- Group roles
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('admin-group', 'Admin');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('admin-group', 'ManageRefData');

INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('chief-registrar-group', 'AssignApps');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('chief-registrar-group', 'ReAssignApps');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('chief-registrar-group', 'Viewing');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('chief-registrar-group', 'ManageRights');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('chief-registrar-group', 'ApproveTrans');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('chief-registrar-group', 'GenerateTitle');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('chief-registrar-group', 'ManageOwners');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('chief-registrar-group', 'ViewReports');

INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('registrar-group', 'AssignApps');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('registrar-group', 'Viewing');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('registrar-group', 'ManageRights');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('registrar-group', 'ManageOwners');

INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('front-office-group', 'ManageApps');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('front-office-group', 'WithdrawApps');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('front-office-group', 'AssignApps');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('front-office-group', 'Viewing');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('front-office-group', 'ManageOwners');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('front-office-group', 'Search');

INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('surveyor-group', 'ManageParcels');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('surveyor-group', 'AssignApps');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('surveyor-group', 'Viewing');

INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('ceo-group', 'Viewing');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('ceo-group', 'Search');
INSERT INTO public.approle_appgroup(group_id, role_code) VALUES ('ceo-group', 'ViewReports');

-- Users
-- sha256 hash. password admin
INSERT INTO public.appuser(id, username, passwd, first_name, last_name, email, mobile_number, active, description)
  VALUES ('admin-user', 'admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'User', 'Admin', null, '', true, 'System administrator');

-- sha256 hash. password super
INSERT INTO public.appuser(id, username, passwd, first_name, last_name, email, mobile_number, active, description)
  VALUES ('super-user', 'su', '73d1b1b1bc1dabfb97f216d897b7968e44b06457920f00f2dc6c1ed3be25ad4c', 'Super', 'User', null, '', true, 'This is super user with all access rights in the system. It has to be deleted in the production environment.');

-- User groups
INSERT INTO public.appuser_appgroup(user_id, group_id) VALUES ('admin-user', 'admin-group');
INSERT INTO public.appuser_appgroup(user_id, group_id) VALUES ('super-user', 'admin-group');
INSERT INTO public.appuser_appgroup(user_id, group_id) VALUES ('super-user', 'chief-registrar-group');
INSERT INTO public.appuser_appgroup(user_id, group_id) VALUES ('super-user', 'registrar-group');
INSERT INTO public.appuser_appgroup(user_id, group_id) VALUES ('super-user', 'front-office-group');
INSERT INTO public.appuser_appgroup(user_id, group_id) VALUES ('super-user', 'surveyor-group');
INSERT INTO public.appuser_appgroup(user_id, group_id) VALUES ('super-user', 'ceo-group');

-- Languages
INSERT INTO public.ref_language(code, val, active, is_default, item_order, ltr) VALUES ('en', 'English', true, true, 1, true);
INSERT INTO public.ref_language(code, val, active, is_default, item_order, ltr) VALUES ('sw', 'Kiswahili', true, false, 2, true);

-- Settings
INSERT INTO public.setting(id, val, active, description) VALUES ('version', '0.1', 't', 'Current version of the database. It is used to check compatibility with application version. If versions does not match, an exception will be thrown');
INSERT INTO public.setting(id, val, active, description) VALUES ('office-code', 'IRD', 't', 'Office code, which will be used for generating various numbers, e.g. application number');
INSERT INTO public.setting(id, val, active, description) VALUES ('file-number-prefix', 'IRD/HW/', 't', 'File number prefix, used for generation of the file (case) number in archive.');
INSERT INTO public.setting(id, val, active, description) VALUES ('district-officer', 'GEOFREY REUBEN KALUWA', 't', 'District officer name. This officer is responsible for signing certificates.');
INSERT INTO public.setting(id, val, active, description) VALUES ('office-district', '119IR', 't', 'District code, where the office operates.');
INSERT INTO public.setting(id, val, active, description) VALUES ('media-path', '../trust_files', 't', 'Folder path where all files related to applications, parties and rights will be stored. If relative path is provided, then web-application root folder will be used as starting point.');
INSERT INTO public.setting(id, val, active, description) VALUES ('max-file-size', '20480', 't', 'Maximum file size in KB that can be uploaded into the system.');
INSERT INTO public.setting(id, val, active, description) VALUES ('file-extensions', 'pdf,doc,docx,xls,xlsx,txt,jpg,jpeg,png,tif,tiff,csv', 't', 'Allowed file extensions for uploading into the system.');

-- Doc types
INSERT INTO public.ref_doc_type(code, val) VALUES ('irs', 'Informal Receipt of Sale::::Hati ya manunuzi isiyo rasmi');
INSERT INTO public.ref_doc_type(code, val) VALUES ('frs', 'Formal Receipt of Sale::::Hati ya manunuzi iliyo rasmi');
INSERT INTO public.ref_doc_type(code, val) VALUES ('al', 'Letter of Allocation::::Barua ya mgao');
INSERT INTO public.ref_doc_type(code, val) VALUES ('pd', 'Probate Document::::Hati ya Mirathi');
INSERT INTO public.ref_doc_type(code, val) VALUES ('oth', 'Other::::Nyingine');

-- Application type groups
INSERT INTO public.ref_app_type_group(code, val) VALUES ('ccro', 'CCRO');
INSERT INTO public.ref_app_type_group(code, val) VALUES ('restrictions', 'Restrictions');
--INSERT INTO public.ref_app_type_group(code, val) VALUES ('rightholders', 'Rightholders');
--INSERT INTO public.ref_app_type_group(code, val) VALUES ('info', 'Information');

-- Transaction types
INSERT INTO public.ref_transaction_type(code, val) VALUES ('reg_ownership', 'Registration of ownership right');
INSERT INTO public.ref_transaction_type(code, val) VALUES ('trans_ownership', 'Transfer of ownership right');
INSERT INTO public.ref_transaction_type(code, val) VALUES ('surrender', 'Surrender of ownership right');
INSERT INTO public.ref_transaction_type(code, val) VALUES ('terminate', 'Termination of ownership right');
INSERT INTO public.ref_transaction_type(code, val) VALUES ('merge', 'Merge of ownership rights');
INSERT INTO public.ref_transaction_type(code, val) VALUES ('split', 'Split of ownership right');
INSERT INTO public.ref_transaction_type(code, val) VALUES ('reg_mortgage', 'Registration of mortgage');
INSERT INTO public.ref_transaction_type(code, val) VALUES ('remove_mortgage', 'Discharge of mortgage');
INSERT INTO public.ref_transaction_type(code, val) VALUES ('reg_caveat', 'Registration of caveat');
INSERT INTO public.ref_transaction_type(code, val) VALUES ('remove_caveat', 'Removal of caveat');

-- Application types
INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code) VALUES ('ccro_new', 'New CCRO', 'ccro', 'reg_ownership');
INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code) VALUES ('ccro_trans', 'Transfer', 'ccro', 'trans_ownership');
INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code) VALUES ('ccro_surrender', 'Surrender', 'ccro', 'surrender');
INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code) VALUES ('ccro_terminate', 'Termination', 'ccro', 'terminate');
INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code) VALUES ('ccro_split', 'Split', 'ccro', 'split');
INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code) VALUES ('ccro_merge', 'Merge', 'ccro', 'merge');
INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code) VALUES ('mortgage_reg', 'Registration of Mortgage', 'restrictions', 'reg_mortgage');
INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code) VALUES ('mortgage_remove', 'Discharge of Mortgage', 'restrictions', 'remove_mortgage');
INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code) VALUES ('caveat_reg', 'Registration of Caveat', 'restrictions', 'reg_caveat');
INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code) VALUES ('caveat_remove', 'Removal of Caveat', 'restrictions', 'remove_caveat');

-- Right type groups
INSERT INTO public.ref_right_type_group(code, val) VALUES ('ownership', 'Ownership');
INSERT INTO public.ref_right_type_group(code, val) VALUES ('restriction', 'Restriction');

-- Right types
INSERT INTO public.ref_right_type(code, val, right_type_group_code, allow_multiple) VALUES ('ccro', 'CCRO', 'ownership', 'f');
INSERT INTO public.ref_right_type(code, val, right_type_group_code, allow_multiple) VALUES ('mortgage', 'Mortgage', 'restriction', 't');
INSERT INTO public.ref_right_type(code, val, right_type_group_code, allow_multiple) VALUES ('caveat', 'Caveat', 'restriction', 't');

-- Application type to right type
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('ccro_new', 'ccro');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('ccro_trans', 'ccro');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('ccro_surrender', 'ccro');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('ccro_terminate', 'ccro');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('ccro_split', 'ccro');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('ccro_merge', 'ccro');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('mortgage_reg', 'mortgage');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('mortgage_remove', 'mortgage');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('caveat_reg', 'caveat');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('caveat_remove', 'caveat');

-- Application status
INSERT INTO public.ref_app_status(code, val) VALUES ('pending', 'Pending');
INSERT INTO public.ref_app_status(code, val) VALUES ('approved', 'Approved');
INSERT INTO public.ref_app_status(code, val) VALUES ('rejected', 'Rejected');
INSERT INTO public.ref_app_status(code, val) VALUES ('withdrawn', 'Withdrawn');

-- Party status
INSERT INTO ref_party_status(code, val) VALUES ('pending', 'Pending');
INSERT INTO ref_party_status(code, val) VALUES ('active', 'Active');
INSERT INTO ref_party_status(code, val) VALUES ('historic', 'Historic');

-- Registration status
INSERT INTO public.ref_reg_status(code, val) VALUES ('pending', 'Pending');
INSERT INTO public.ref_reg_status(code, val) VALUES ('current', 'Registered');
INSERT INTO public.ref_reg_status(code, val) VALUES ('historic', 'Historic');

-- Citizenships
INSERT INTO public.ref_citizenship(code, val) VALUES ('tz', 'Tanzania::::Mtanzania');
INSERT INTO public.ref_citizenship(code, val) VALUES ('other', 'Other::::Nyingine');

-- ID type
INSERT INTO public.ref_id_type(code, val) VALUES ('voter_id', 'Voter ID::::Kitambulisho cha mpiga kura');
INSERT INTO public.ref_id_type(code, val) VALUES ('drive_lic', 'Driving license::::Leseni ya udereva');
INSERT INTO public.ref_id_type(code, val) VALUES ('passport', 'Passport::::Pasi ya kusafiria');
INSERT INTO public.ref_id_type(code, val) VALUES ('id_card', 'ID card::::Kitambulisho cha Mkazi');
INSERT INTO public.ref_id_type(code, val) VALUES ('other', 'Other::::Nyingine');

-- Marital status
INSERT INTO public.ref_marital_status(code, val) VALUES ('unmarried', 'un-married::::Hajaoa/hajaolewa');
INSERT INTO public.ref_marital_status(code, val) VALUES ('married', 'married::::ndoa');
INSERT INTO public.ref_marital_status(code, val) VALUES ('divorced', 'divorced::::talaka');
INSERT INTO public.ref_marital_status(code, val) VALUES ('widow', 'widow::::mjane');
INSERT INTO public.ref_marital_status(code, val) VALUES ('widower', 'widower::::mgane');

-- Gender
INSERT INTO public.ref_gender(code, val) VALUES ('male', 'Male::::kiume');
INSERT INTO public.ref_gender(code, val) VALUES ('female', 'Female::::kike');

-- Entity type
INSERT INTO public.ref_entity_type(code, val) VALUES ('civic', 'Civic::::Taasisi ya kiraia');
INSERT INTO public.ref_entity_type(code, val) VALUES ('mosque', 'Mosque::::Taasisi za kidini');
INSERT INTO public.ref_entity_type(code, val) VALUES ('assosiation', 'Association::::Taasisi za kielimu');
INSERT INTO public.ref_entity_type(code, val) VALUES ('cooperative', 'Cooperative::::Shirika');
INSERT INTO public.ref_entity_type(code, val) VALUES ('other', 'Other::::Nyengine');

-- Land types
INSERT INTO public.ref_land_type(code, val) VALUES ('flat', 'Flat/Plain::::Tambarare');
INSERT INTO public.ref_land_type(code, val) VALUES ('sloping', 'Sloping::::Mteremko');
INSERT INTO public.ref_land_type(code, val) VALUES ('mountainous', 'Mountainous::::Milima');
INSERT INTO public.ref_land_type(code, val) VALUES ('valley', 'Valley::::Bonde');

-- Parcel statuses
INSERT INTO public.ref_parcel_status(code, val) VALUES ('pending', 'Pending');
INSERT INTO public.ref_parcel_status(code, val) VALUES ('active', 'Active');
INSERT INTO public.ref_parcel_status(code, val) VALUES ('historic', 'Historic');

-- Occupany types
INSERT INTO public.ref_occupancy_type(code, val) VALUES ('common', 'Co-occupancy (Tenancy in Common)::::Milki ya pamoja kwa Hisa');
INSERT INTO public.ref_occupancy_type(code, val) VALUES ('single', 'Single Occupant::::Milki ya mmoja');
INSERT INTO public.ref_occupancy_type(code, val) VALUES ('joint', 'Co-occupancy (Joint tenancy)::::Milki ya pamoja isiyogawanyika');
INSERT INTO public.ref_occupancy_type(code, val) VALUES ('probate', 'Probate Administration::::Milki chini ya usimamizi wa Mirathi');
INSERT INTO public.ref_occupancy_type(code, val) VALUES ('guardian', 'Guardian(Minor)::::Milki chini ya Mlezi');
INSERT INTO public.ref_occupancy_type(code, val) VALUES ('nonnatural', 'Non-Natural/Institution::::Taasisi/Kampuni/Asasi');

-- Land uses
INSERT INTO public.ref_landuse(code, val) VALUES ('agriculture', 'Agriculture::::Kilimo');
INSERT INTO public.ref_landuse(code, val) VALUES ('forestry', 'Forestry::::Hifadhi ya Misitu');
INSERT INTO public.ref_landuse(code, val) VALUES ('residential', 'Residential::::Makazi');
INSERT INTO public.ref_landuse(code, val) VALUES ('grazing', 'Grazing::::Malisho');
INSERT INTO public.ref_landuse(code, val) VALUES ('commercial', 'Commercial::::Biashara');
INSERT INTO public.ref_landuse(code, val) VALUES ('public', 'Community Service::::Huduma za jamii');
INSERT INTO public.ref_landuse(code, val) VALUES ('grassland', 'Grassland::::Nyika');
INSERT INTO public.ref_landuse(code, val) VALUES ('industrial', 'Industrial::::Viwanda');
INSERT INTO public.ref_landuse(code, val) VALUES ('conservation', 'Conservation::::Hifadhi');
INSERT INTO public.ref_landuse(code, val) VALUES ('resagri', 'Residential and agricultural::::Makazi na kilimo');
INSERT INTO public.ref_landuse(code, val) VALUES ('wildlife', 'Wildlife/Tourism::::Hifadhi za wanyama na Utalii');
INSERT INTO public.ref_landuse(code, val) VALUES ('social', 'Social services::::Huduma za jamii');
INSERT INTO public.ref_landuse(code, val) VALUES ('mining', 'Mining::::Maeneo ya madini');




















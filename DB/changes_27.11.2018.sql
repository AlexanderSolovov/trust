ALTER TABLE public.ref_app_type
  ADD COLUMN fee double precision NOT NULL DEFAULT 0;
COMMENT ON COLUMN public.ref_app_type.fee IS 'Application fee';

ALTER TABLE history.ref_app_type
  ADD COLUMN fee double precision;

ALTER TABLE public.application
  ADD COLUMN fee double precision NOT NULL DEFAULT 0;
COMMENT ON COLUMN public.application.fee IS 'Application fee';

ALTER TABLE history.application
  ADD COLUMN fee double precision;

-- New applications

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('ccro_trans_admin', 'Transmission of CCRO to Administrator', 'ccro', 'transfer', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('ccro_trans_admin', 'ccro');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('ccro_trans_assent', 'Transmission of CCRO under Assent to the Bequest', 'ccro', 'transfer', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('ccro_trans_assent', 'ccro');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('ccro_trans_survivor', 'Transmission of CCRO to Survivor', 'ccro', 'transfer', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('ccro_trans_survivor', 'ccro');

INSERT INTO public.ref_transaction_type(code, val, active) VALUES ('no_action', 'No Action', 't');

INSERT INTO public.ref_transaction_type(code, val, active) VALUES ('split', 'Split', 't');

INSERT INTO public.ref_transaction_type(code, val, active) VALUES ('merge', 'Merge', 't');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('ccro_search', 'Official Search', 'ccro', 'no_action', 0, 't');

INSERT INTO public.ref_right_type(code, val, right_type_group_code, allow_multiple)
    VALUES ('lease', 'Lease', 'restriction', 't');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('lease_reg', 'Registration of Lease', 'restrictions', 'registration', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('lease_reg', 'lease');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('lease_terminate', 'Termination of Lease', 'restrictions', 'remove', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('lease_terminate', 'lease');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('easement_reg', 'Registration of Easement', 'restrictions', 'registration', 0, 't');

INSERT INTO public.ref_right_type(code, val, right_type_group_code, allow_multiple)
    VALUES ('easement', 'Easement', 'restriction', 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('easement_reg', 'easement');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('easement_terminate', 'Termination of Easement', 'restrictions', 'remove', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('easement_terminate', 'easement');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('injunction_reg', 'Registration of Injunction', 'restrictions', 'registration', 0, 't');

INSERT INTO public.ref_right_type(code, val, right_type_group_code, allow_multiple)
    VALUES ('injunction', 'Injunction', 'restriction', 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('injunction_reg', 'injunction');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('injunction_terminate', 'Termination of Injunction', 'restrictions', 'remove', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('injunction_terminate', 'injunction');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('charge_reg', 'Registration of Charge', 'restrictions', 'registration', 0, 't');

INSERT INTO public.ref_right_type(code, val, right_type_group_code, allow_multiple)
    VALUES ('charge', 'Charge', 'restriction', 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('charge_reg', 'charge');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('charge_terminate', 'Termination of Charge', 'restrictions', 'remove', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('charge_terminate', 'charge');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('change_name', 'Change of Name', 'ccro', 'change_name', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('change_name', 'ccro');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('change_name', 'assign');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('change_name', 'caveat');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('change_name', 'charge');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('change_name', 'easement');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('change_name', 'injunction');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('change_name', 'lease');
INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('change_name', 'mortgage');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('adverse_possession', 'Adverse Possession', 'ccro', 'transfer', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('adverse_possession', 'ccro');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('ccro_renewal', 'CCRO Renewal', 'ccro', 'vary', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('ccro_renewal', 'ccro');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('depo_notice_reg', 'Notice of CCRO Deposit', 'restrictions', 'registration', 0, 't');

INSERT INTO public.ref_right_type(code, val, right_type_group_code, allow_multiple)
    VALUES ('depo_notice', 'Notice of Deposit', 'restriction', 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('depo_notice_reg', 'depo_notice');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('depo_terminate', 'Withdrawal of CCRO Deposit Notice', 'restrictions', 'remove', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('depo_terminate', 'depo_notice');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('ccro_exchange', 'Deed of Exchange', 'ccro', 'transfer', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('ccro_exchange', 'ccro');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('ccro_copy', 'CCRO Copy', 'ccro', 'no_action', 0, 't');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('ccro_revocation', 'Revocation of CCRO', 'ccro', 'terminate', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('ccro_revocation', 'ccro');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('ccro_split', 'CCRO Division', 'ccro', 'split', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('ccro_split', 'ccro');

INSERT INTO public.ref_app_type(code, val, app_type_group_code, transaction_type_code, fee, active)
    VALUES ('ccro_merge', 'CCRO Combination', 'ccro', 'merge', 0, 't');

INSERT INTO public.ref_app_type_right_type(app_type_code, right_type_code) VALUES ('ccro_merge', 'ccro');

-- New document types

INSERT INTO public.ref_doc_type(code, val, active) VALUES ('assent', 'Assent to the Bequest::::Ushauri wa Bequest', 't');

update public.setting set val = '0.2' where id = 'version';

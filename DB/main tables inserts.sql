INSERT INTO public.parcel(id, land_type_code, uka, survey_date, hamlet_code, address, geom)
    VALUES ('p1', 'flat', 'AU/12234', '2017-01-01', '47VUKA-VUKA', 'Chukotka 11', st_geomfromtext('POLYGON((-71.1776585052917 42.3902909739571,-71.1776820268866 42.3903701743239,
-71.1776063012595 42.3903825660754,-71.1775826583081 42.3903033653531,-71.1776585052917 42.3902909739571))',4326));

INSERT INTO public.property(id, parcel_id, reg_date)
    VALUES ('prop1', 'p1', '2017-01-01');

INSERT INTO public.rrr(id, property_id, right_type_code, occupancy_type_code, 
            allocation_date, reg_date, start_date, end_date, duration, annual_fee, 
            intereset_rate, declared_landuse_code, approved_landuse_code)
    VALUES ('rrr1', 'prop1', 'ccro', 'single', '2017-01-01', '2017-01-01', '2017-01-01', '2027-01-01', 10, 1000, 0, 'residential', 'residential');

INSERT INTO public.rrr(id, property_id, right_type_code, reg_date, start_date, end_date, duration, intereset_rate)
    VALUES ('rrr2', 'prop1', 'mortgage', '2017-01-01', '2017-01-01', '2027-01-01', 10, 10.0);





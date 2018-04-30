-- Regions
--insert into public.ref_region (code, val) values ('23MRG', 'Morogoro');
insert into public.ref_region (code, val) values ('IR', 'Iringa');

-- Districts
--insert into public.ref_district (code, val, region_code) values ('23MRG', 'Kilombero', 'mor');
insert into public.ref_district (code, val, region_code) values ('IRA', 'Iringa Rural', 'IR');

--insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('KYW', 'Kitayawa', 'IRA', 'Kitayawa, S.L.P. 108', 'OMARI JOSEPH KINDOLE', 'TINA SADIKI KIMAMBO');
--insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('IGG', 'Itagutwa', 'IRA', 'Itagutwa, S.L.P. 108', 'Issa Simaliwa Lubava', 'Suphi Ernest Mfilinge');
--insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('0000', 'Ilalasimba', 'IRA', '00000', 'VC', 'EO');
--insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('KNG', 'Kinywangaanga', 'IRA', 'Kinywangaanga, S.L.P 108', 'ADAMU MODESTUSY NGELIME', 'JACKSON JOSEPH KISIBO');
insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('92IRA', 'Kiponzelo', 'IRA', 'Kiponzelo, S.L.P 108', 'ALLEN ZAVERY DALLU', 'LUI THADEUS BONIFACE');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('92IRA-MKW', 'MKWAWA', 'MKW', '92IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('92IRA-MLS', 'MSALASI', 'MLS', '92IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('92IRA-MDK', 'MADUKANI', 'MDK', '92IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('92IRA-SYL', 'SIYOVELWA', 'SYL', '92IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('92IRA-GDY', 'GENDAWUYE', 'GDY', '92IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('92IRA-KSN', 'KANISANI', 'KSN', '92IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('92IRA-MJM', 'MJIMWEMA', 'MJM', '92IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('92IRA-NGW', 'NGONGWA', 'NGW', '92IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('92IRA-SHN', 'SHULENI', 'SHN', '92IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('92IRA-LMB', 'LUMUMBA', 'LMB', '92IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('92IRA-MSB', 'MSOMBE', 'MSB', '92IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('109IRA', 'Magunga', 'IRA', 'Magunga, S.L.P.  108', 'MAIKO TEUDOSI MGIMWA', 'JIMMY PETER CHANDAFA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('109IRA-IHM', 'IHAMI', 'IHM', '109IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('109IRA-MGA', 'MGOGONDELE-A', 'MGA', '109IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('109IRA-MGB', 'MGOGONDELE-B', 'MGB', '109IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('109IRA-MYL', 'MINYALA', 'MYL', '109IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('109IRA-LGL', 'LUGAILO', 'LGL', '109IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('109IRA-NGK', 'NGUVUKAZI', 'NGK', '109IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('102IRA', 'Usengelindete', 'IRA', 'Usengelindete, S.L.P.  108, Iringa', 'MANENO WILLIAM MAKETA', 'EDMUND EDWARD KASANGA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('102IRA-IGL', 'IGULA', 'IGL', '102IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('102IRA-UWG', 'UMWAGA', 'UWG', '102IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('102IRA-ITB', 'ITIMBO', 'ITB', '102IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('102IRA-KGS', 'KIGASA', 'KGS', '102IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('43IRA', 'Mgama', 'IRA', 'Mgama, S.L.P.  108', 'JOHN JOSEPH KITIKE', 'HAPPINES DAUD MKONGWA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('43IRA-KHS', 'KIHESA', 'KHS', '43IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('43IRA-MBM', 'MBALAMO', 'MBM', '43IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('43IRA-MYW', 'MYOMBWE', 'MYW', '43IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('43IRA-MHT', 'MHAGATI', 'MHT', '43IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('43IRA-WGM', 'WANGAMA', 'WGM', '43IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('43IRA-ISB', 'ISOMBE', 'ISB', '43IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('43IRA-MGA', 'MGAMA-A', 'MGA', '43IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('43IRA-MGB', 'MGAMA-B', 'MGB', '43IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('43IRA-KTA', 'KATENGE', 'KTA', '43IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('43IRA-MSK', 'MSICHOKE', 'MSK', '43IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('43IRA-WLW', 'WILOWESI', 'WLW', '43IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('43IRA-KTB', 'KATENGE', 'KTB', '43IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('46IRA', 'Malagosi', 'IRA', 'Malagosi, S.L.P. 108', 'FREDI LUKA MARUHA KIBIKI', 'FERUZI JOSEPH MBASI');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('46IRA-MGO', 'MALAGOSI', 'MGO', '46IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('46IRA-KHW', 'KIHAWA', 'KHW', '46IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('46IRA-IBL', 'IBUMILA', 'IBL', '46IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('46IRA-IVM', 'ITOVAKAMI', 'IVM', '46IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('46IRA-IGS', 'IGANGASENGA', 'IGS', '46IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('46IRA-NZL', 'NZIVILA', 'NZL', '46IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('46IRA-MTL', 'MAMBAMATALI', 'MTL', '46IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('46IRA-MSB', 'MSOMBE', 'MSB', '46IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('46IRA-WTL', 'WOTALISOLI', 'WTL', '46IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('5IRA', 'Mfukulembe', 'IRA', 'Mfukulembe, S.L.P. 108', 'GERADI MICHAEL KIMALILA', 'GALUSI KITOGOTA KALOLE');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('5IRA-LYS', 'LYASA', 'LYS', '5IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('5IRA-NDL', 'NDOLELA', 'NDL', '5IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('5IRA-IGA', 'IGULUMUTI-A', 'IGA', '5IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('5IRA-IGB', 'IGULUMUTI-B', 'IGB', '5IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('91IRA', 'Udumka', 'IRA', 'Udumuka, S.L.P. 108, Iringa', 'ALFONCE WILBERT NYAGAWA', 'ATUPOKILE ADAMSON MWAMBUNGU');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('91IRA-IKG', 'IKUNGU', 'IKG', '91IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('91IRA-UTL', 'UTULO', 'UTL', '91IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('91IRA-OFN', 'OFISINI', 'OFN', '91IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('44IRA', 'Ilandutwa', 'IRA', 'Ilandutwa, S.L.P. 108', 'RASHID LAURENT MIHO', 'ANDERSON LEONARD MHOMISOLI');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('44IRA-NDL', 'NDOLELA', 'NDL', '44IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('44IRA-KWL', 'KILEWELA', 'KWL', '44IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('44IRA-LGL', 'LUGOLOLELO', 'LGL', '44IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('44IRA-LGF', 'LUGOFU', 'LGF', '44IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('44IRA-MYG', 'MAYUGI', 'MYG', '44IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('44IRA-NKA', 'NYAKATULI-A', 'NKA', '44IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('44IRA-NKB', 'NYAKATULI-B', 'NKB', '44IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('72IRA', 'Muwimbi', 'IRA', 'Muwimbi, S.L.P. 108', 'RODELIGO KASIYANI MBWALE', 'ROMANUS MICHAEL SAMBALA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('72IRA-KGM', 'KIBUGUMO', 'KGM', '72IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('72IRA-ULT', 'ULETE', 'ULT', '72IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('72IRA-GZL', 'GEZAULOLE', 'GZL', '72IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('72IRA-MWM', 'MUWIMBI', 'MWM', '72IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('59IRA', 'Nyamihuu', 'IRA', 'Nyamihuu, S.L.P. 108, IRINGA', 'ONAM WILLIAM MGENI', 'ALOYCE GRAYSON CHEYO');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('59IRA-MTN', 'MABATINI', 'MTN', '59IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('59IRA-IGL', 'IGANGIMALI', 'IGL', '59IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('59IRA-MGL', 'MLANGALI', 'MGL', '59IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('59IRA-WLS', 'WILOLESI', 'WLS', '59IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('59IRA-MGO', 'MAKANYAGIO', 'MGO', '59IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('59IRA-CHM', 'CHEMCHEM', 'CHM', '59IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('59IRA-SPL', 'ISUPILO', 'SPL', '59IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('59IRA-KHW', 'KILIMAHEWA', 'KHW', '59IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('59IRA-MBY', 'MBUYUNI', 'MBY', '59IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('59IRA-MJG', 'MAJENGO', 'MJG', '59IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('59IRA-ISL', 'ISALA', 'ISL', '59IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('108IRA', 'Mwambao', 'IRA', 'Mwambao, S.L.P 108, IRINGA', 'CHARLESY LUKOMBES NZIKU', 'JOHN NAFTARI SANGA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('108IRA-IDT', 'IDETE', 'IDT', '108IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('108IRA-KHW', 'KILIMAHEWA', 'KHW', '108IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('108IRA-MWA', 'MWAMBAO-A', 'MWA', '108IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('108IRA-MWB', 'MWAMBAO-B', 'MWB', '108IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('10IRA', 'Lwato', 'IRA', 'Lwato, S.L.P. 108, IRINGA', 'BATHOLOMEO MWENGWA', 'JOHN THEODORY MHAGAMA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('10IRA-LWA', 'LWATO-A', 'LWA', '10IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('10IRA-LWB', 'LWATO-B', 'LWB', '10IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('10IRA-LGL', 'LUGOLOLELO', 'LGL', '10IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('4IRA', 'Ngano', 'IRA', 'Ngano, S.L.P 108, IRINGA', 'ZAKARIA JULIAS  WIMBE', 'AUGUSTINO SAMWEL MTEGA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('4IRA-MNG', 'MWANG''INGO', 'MNG', '4IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('4IRA-GDB', 'GODOWN-B', 'GDB', '4IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('4IRA-LVY', 'LYAVEYA', 'LVY', '4IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('4IRA-GDA', 'GODOWN-A', 'GDA', '4IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('55IRA', 'Makota', 'IRA', 'Makota, S.L.P 108, IRINGA', 'MARIO STEPHAN KILIENYI', 'SAMEHE LUCAS KIPINGI');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('55IRA-LHU', 'LUHUU', 'LHU', '55IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('55IRA-MPW', 'MPWAPWA', 'MPW', '55IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('55IRA-MNG-A', 'MANG''ULA-A', 'MNG-A', '55IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('55IRA-MNG-B', 'MANG''ULA-B', 'MNG-B', '55IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('55IRA-ITM', 'ITIKANGOMBWE', 'ITM', '55IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('94IRA', 'Ikungwe', 'IRA', 'Ikungwe, S.L.P 108, IRINGA', 'EMANUEL KASPA  MAVIKA', 'GALASIANO ENDWADI KAPOMA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('94IRA-MKZ', 'MKUNZI', 'MKZ', '94IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('94IRA-MGO', 'MAKANYAGIO', 'MGO', '94IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('94IRA-MKT', 'MKUTA', 'MKT', '94IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('94IRA-IKW', 'IKUNGWE', 'IKW', '94IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('94IRA-TMB', 'TAMBALANG''OMBE', 'TMB', '94IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('19IRA', 'Isele', 'IRA', 'Isele, S.L.P 108, IRINGA', 'MBARAKA NZALAMOTO', 'YOKEBEDY DAUDI PANGAWE');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('19IRA-KSK', 'KISOLOKA', 'KSK', '19IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('19IRA-KLH', 'KIKULUHE', 'KLH', '19IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('19IRA-ISL', 'ISELE', 'ISL', '19IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('19IRA-CGZ', 'CHALIGANZA', 'CGZ', '19IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('19IRA-MGM', 'MBINGAMA', 'MGM', '19IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('81IRA', 'Chamdindi', 'IRA', 'Chamndindi, S.L.P 108, IRINGA', 'SAID OLE MLIMAA', 'MARTHA THOMAS MIGODELA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('81IRA-IKG', 'IKWEGA', 'IKG', '81IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('81IRA-NKL', 'NYAKANSILO', 'NKL', '81IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('81IRA-KTI', 'KATI', 'KTI', '81IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('81IRA-MGN', 'MIEGAMANO', 'MGN', '81IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('81IRA-KBN', 'KIBAONI', 'KBN', '81IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('81IRA-KPG', 'KIPANGA', 'KPG', '81IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('81IRA-MWL', 'MYOWELA', 'MWL', '81IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('51IRA', 'Weru', 'IRA', 'Weru, S.L.P 108, IRINGA', 'FADHILI  MWISOMBE', 'SALMA KITENGE');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('51IRA-MGW', 'MAGANGWE', 'MGW', '51IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('51IRA-KNL', 'KILANGALI', 'KNL', '51IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('51IRA-IPN', 'IPANGANI', 'IPN', '51IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('51IRA-MWP', 'MWAGAMAPESA', 'MWP', '51IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('51IRA-MSK', 'MSEKE', 'MSK', '51IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('26IRA', 'Kisanga', 'IRA', 'Kisanga, S.L.P 108, IRINGA', 'MEJA E. LUBANO', 'BARAKA MEELA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('26IRA-ILY', 'ILAMBALYELU', 'ILY', '26IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('26IRA-KSN', 'KISANGA', 'KSN', '26IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('26IRA-KNL', 'KILANGILILO', 'KNL', '26IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('26IRA-MWD', 'MAWINDI', 'MWD', '26IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('26IRA-LNK', 'LYANIKA', 'LNK', '26IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('26IRA-KLL', 'KILALA', 'KLL', '26IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('26IRA-MGG', 'MAGWAGU', 'MGG', '26IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('128IRA', 'Matembo', 'IRA', 'Matembo, S.L.P 108, IRINGA', 'JACOB PHILIP MAHALI', 'FREDY MESHACK MNYAWAMI');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('128IRA-JGN', 'JANGWANI', 'JGN', '128IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('128IRA-MTJ', 'MTAKUJA', 'MTJ', '128IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('128IRA-SWT', 'SOWETO', 'SWT', '128IRA');

--insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('55MRG', 'Idete', 'kilod', 'Idete, P.O. Box 22, Morogoro', 'FADHILI SOMBE', 'SALMA KITENGE');
--insert into public.ref_hamlet (code, val, abbr, village_code) values ('55MRG-MGW', 'MAGANGWE', 'MGW', '55MRG');
--insert into public.ref_hamlet (code, val, abbr, village_code) values ('55MRG-IPN', 'IPANGANI', 'IPN', '55MRG');
--insert into public.ref_hamlet (code, val, abbr, village_code) values ('55MRG-MWP', 'MWAGAMAPESA', 'MWP', '55MRG');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('00IRA', 'Mlanda', 'IRA', 'Mlanda, S.L.P 108, IRINGA', 'VITALIS MARIO SAMILA', 'GASTO MANFRED MGENI');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('00IRA-MLB', 'MLANDA-B', 'MLB', '00IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('00IRA-NLW', 'NYALAWE', 'NLW', '00IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('00IRA-MSB', 'MSOMBE', 'MSB', '00IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('00IRA-MLA', 'MLANDA-A', 'MLA', '00IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('00IRA-ILB', 'ILEMBULA', 'ILB', '00IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('00IRA-UKG', 'UKANG''A', 'UKG', '00IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('115IRA', 'Igunda', 'IRA', 'Igunda, S.L.P 108, IRINGA', 'MTULI P. MBUGI', 'HALIMA MSIGWA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('115IRA-ILA', 'ILAMBA', 'ILA', '115IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('115IRA-MLL', 'MLOLO', 'MLL', '115IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('115IRA-LKG', 'LUKINGI', 'LKG', '115IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('115IRA-LMM', 'LUMUMA', 'LMM', '115IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('93IRA', 'Ihomasa', 'IRA', 'Ihomasa, S.L.P 108, IRINGA', 'PETRO AUGUSTINO NGWALE', 'FRANK AUGUSTINO MBANDO');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('93IRA-VKL', 'VIKULA', 'VKL', '93IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('93IRA-MKO', 'MKONDOA', 'MKO', '93IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('93IRA-MUN', 'MUUNGANO', 'MUN', '93IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('93IRA-IHS', 'IHOMASA', 'IHS', '93IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('93IRA-LND', 'LUPANDE', 'LND', '93IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('6IRA', 'Kitapilimwa', 'IRA', 'Kitapilimwa, S.L.P 108, IRINGA', 'ABAS MGWILANGA', 'FAUSTINO MHAGAMA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('6IRA-IKG', 'IKINGO', 'IKG', '6IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('6IRA-LGL', 'LUGALO', 'LGL', '6IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('6IRA-KMD', 'KINYAMADUMA', 'KMD', '6IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('1IRA', 'Kiwere', 'IRA', 'Kiwere, S.L.P 108, IRINGA', 'YUSTIN P. LIPITA', 'ALOYCE J. SHIRIMA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('1IRA-CPK', 'CHAPAKAZI', 'CPK', '1IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('1IRA-MKD', 'MAKONDO', 'MKD', '1IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('1IRA-MWA', 'MWAYA-A', 'MWA', '1IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('1IRA-MWB', 'MWAYA-B', 'MWB', '1IRA');

insert into public.ref_village (code, val, district_code, address, chairman, executive_officer) values ('117IRA', 'Lupembe Lwasenga', 'IRA', 'Lupembelwasenga, S.L.P 108, IRINGA', 'EZEKIA MHEHE', 'METHOD MHALUKA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('117IRA-IMY', 'IMALINYI', 'IMY', '117IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('117IRA-MHG', 'MHEGELE', 'MHG', '117IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('117IRA-RUH', 'RUAHA', 'RUH', '117IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('117IRA-IGM', 'IGONITAMWA', 'IGM', '117IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('117IRA-IFA', 'IFIAVANU-A', 'IFA', '117IRA');
insert into public.ref_hamlet (code, val, abbr, village_code) values ('117IRA-IFB', 'IFIAVANU-B', 'IFB', '117IRA');

-- IMPORTANT: THIS UPDATE IS NOT YET FINISHED

-- Queries that are pending an update should be placed here
-- this allows them to be verified as working together before being
-- migrated to the DML scripts

-- update 35

-- new corrected ships dock locs
DELETE FROM dungeon WHERE src_x = "33423" and src_y = "33502" and src_mapid = "4"; 
DELETE FROM dungeon WHERE src_x = "32735" and src_y = "32794" and src_mapid = "83"; 
DELETE FROM dungeon WHERE src_x = "32936" and src_y = "33058" and src_mapid = "70"; 
DELETE FROM dungeon WHERE src_x = "32732" and src_y = "32796" and src_mapid = "84"; 
DELETE FROM dungeon WHERE src_x = "32738" and src_y = "32852" and src_mapid = "445"; 
DELETE FROM dungeon WHERE src_x = "32750" and src_y = "32874" and src_mapid = "445"; 
DELETE FROM dungeon WHERE src_x = "32732" and src_y = "32796" and src_mapid = "447"; 
DELETE FROM dungeon WHERE src_x = "32297" and src_y = "33087" and src_mapid = "440"; 
DELETE FROM dungeon WHERE src_x = "32735" and src_y = "32794" and src_mapid = "446"; 

INSERT INTO `dungeon` VALUES ('33423', '33502', '4', '32735', '32795', '83', '4', ''); 
INSERT INTO `dungeon` VALUES ('33424', '33502', '4', '32735', '32795', '83', '4', ''); 
INSERT INTO `dungeon` VALUES ('33425', '33502', '4', '32735', '32795', '83', '4', ''); 
INSERT INTO `dungeon` VALUES ('33426', '33502', '4', '33735', '32795', '83', '4', ''); 
INSERT INTO `dungeon` VALUES ('32733', '32794', '83', '33425', '33501', '4', '0', ''); 
INSERT INTO `dungeon` VALUES ('32734', '32794', '83', '33425', '33501', '4', '0', ''); 
INSERT INTO `dungeon` VALUES ('32735', '32794', '83', '33425', '33501', '4', '0', ''); 
INSERT INTO `dungeon` VALUES ('32736', '32794', '83', '33425', '33501', '4', '0', ''); 
INSERT INTO `dungeon` VALUES ('32935', '33058', '70', '32734', '32797', '84', '4', ''); 
INSERT INTO `dungeon` VALUES ('32936', '33058', '70', '32734', '32797', '84', '4', ''); 
INSERT INTO `dungeon` VALUES ('32937', '33058', '70', '32734', '32797', '84', '4', ''); 
INSERT INTO `dungeon` VALUES ('32732', '32796', '84', '32936', '33057', '70', '0', ''); 
INSERT INTO `dungeon` VALUES ('32733', '32796', '84', '32936', '33057', '70', '0', ''); 
INSERT INTO `dungeon` VALUES ('32734', '32796', '84', '32936', '33057', '70', '0', ''); 
INSERT INTO `dungeon` VALUES ('32735', '32796', '84', '32936', '33057', '70', '0', ''); 
INSERT INTO `dungeon` VALUES ('32738', '32851', '445', '32738', '32475', '4', '2', ''); 
INSERT INTO `dungeon` VALUES ('32739', '32851', '445', '32738', '32475', '4', '2', ''); 
INSERT INTO `dungeon` VALUES ('32750', '32874', '445', '32732', '32797', '447', '4', ''); 
INSERT INTO `dungeon` VALUES ('32751', '32874', '445', '32732', '32797', '447', '4', ''); 
INSERT INTO `dungeon` VALUES ('32752', '32874', '445', '32732', '32797', '447', '4', ''); 
INSERT INTO `dungeon` VALUES ('32731', '32796', '447', '32751', '32873', '445', '0', ''); 
INSERT INTO `dungeon` VALUES ('32732', '32796', '447', '32751', '32873', '445', '0', ''); 
INSERT INTO `dungeon` VALUES ('32733', '32796', '447', '32751', '32873', '445', '0', ''); 
INSERT INTO `dungeon` VALUES ('32296', '33087', '440', '32735', '32795', '446', '4', ''); 
INSERT INTO `dungeon` VALUES ('32297', '33087', '440', '32735', '32795', '446', '4', ''); 
INSERT INTO `dungeon` VALUES ('32298', '33087', '440', '32735', '32795', '446', '4', ''); 
INSERT INTO `dungeon` VALUES ('32735', '32794', '446', '32297', '33086', '440', '0', ''); 
INSERT INTO `dungeon` VALUES ('32736', '32794', '446', '32297', '33086', '440', '0', ''); 
INSERT INTO `dungeon` VALUES ('32630', '32983', '0', '32733', '32797', '5', '4', ''); 
INSERT INTO `dungeon` VALUES ('32631', '32983', '0', '32733', '32797', '5', '4', ''); 
INSERT INTO `dungeon` VALUES ('32632', '32983', '0', '32733', '32797', '5', '4', ''); 
INSERT INTO `dungeon` VALUES ('32733', '32796', '5', '32631', '32982', '0', '0', ''); 
INSERT INTO `dungeon` VALUES ('32734', '32796', '5', '32631', '32982', '0', '0', ''); 
INSERT INTO `dungeon` VALUES ('32540', '32728', '4', '32735', '32795', '6', '4', ''); 
INSERT INTO `dungeon` VALUES ('32542', '32728', '4', '32735', '32795', '6', '4', ''); 
INSERT INTO `dungeon` VALUES ('32543', '32728', '4', '32735', '32795', '6', '4', ''); 
INSERT INTO `dungeon` VALUES ('32544', '32728', '4', '32735', '32795', '6', '4', ''); 
INSERT INTO `dungeon` VALUES ('32734', '32794', '6', '32544', '32727', '4', '0', ''); 
INSERT INTO `dungeon` VALUES ('32735', '32794', '6', '32544', '32727', '4', '0', ''); 
INSERT INTO `dungeon` VALUES ('32736', '32794', '6', '32544', '32727', '4', '0', '');

-- oren Franco gamble corrected live prices
delete from shop where npc_id = 70068;

INSERT INTO `shop` VALUES (70068, 41, 0, 95000, 0, -1);
INSERT INTO `shop` VALUES (70068, 148, 1, 88000, 0, -1);
INSERT INTO `shop` VALUES (70068, 142, 2, 83000, 0, -1);
INSERT INTO `shop` VALUES (70068, 29, 3, 88000, 0, -1);
INSERT INTO `shop` VALUES (70068, 129, 4, 77000, 0, -1);
INSERT INTO `shop` VALUES (70068, 125, 5, 84000, 0, -1);
INSERT INTO `shop` VALUES (70068, 20125, 6, 63000, 0, -1);
INSERT INTO `shop` VALUES (70068, 20011, 7, 58000, 0, -1);
INSERT INTO `shop` VALUES (70068, 20036, 8, 69000, 0, -1);
INSERT INTO `shop` VALUES (70068, 20013, 9, 81000, 0, -1);
INSERT INTO `shop` VALUES (70068, 20014, 10, 73000, 0, -1);
INSERT INTO `shop` VALUES (70068, 20015, 11, 76000, 0, -1);

-- npc chat delay correction
UPDATE npcchat SET chat_interval="20000";

-- new skill names
update skills set name = 'Storm Walk' where skill_id = 172;
update skills set name = 'Pollute Water' where skill_id = 173;
update skills set name = 'Striker Gale' where skill_id = 174;
update skills set name = 'Soul of Flame' where skill_id = 175;
update skills set name = 'Additional Fire' where skill_id = 176;

-- remove all potentional infinite money exploits via shop buying/selling
update shop set selling_price = 1 where item_id = 20322 and selling_price = 0;
update shop set purchasing_price = 1 where npc_id = 70004 and item_id = 20322;
update shop set purchasing_price = 500 where npc_id = 70079 and item_id = 40089;
update shop set purchasing_price = 500 where npc_id = 70014 and item_id = 40089;
update shop set selling_price = 23000 where npc_id = 70058 and item_id = 20101;
update shop set selling_price = 200 where npc_id = 70058 and item_id = 20101;
update shop set selling_price = 8 where npc_id = 70005 and item_id = 4;
update shop set selling_price = 2 where npc_id = 70093 and item_id = 40740;

-- gm spell book shop shouldn't buy stuff for more than he sells
update shop set purchasing_price = -1 where npc_id = 81022;

-- Halloween Event Shop for mask and candy
INSERT INTO shop VALUES ('81123', '20046', '0', '5000', '0', '1');
INSERT INTO shop VALUES ('81123', '40725', '0', '5', '0', '1');

-- added html for jack-o-lantern
update npcaction set normal_action = 'pump1' where npcid = 81123;
update npcaction set caotic_action = 'pump1' where npcid = 81123;

-- add drop for jack-o-lantern
insert into droplist values ('45166', '40721', '1', '1', '10000');

-- added pick up for jack-o-lantern
update npc set picupitem = '1' where npcid = 45166;
update npc set picupitem = '1' where npcid = 45167;


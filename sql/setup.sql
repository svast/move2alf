create database move2alf;
create database move2alf_test;
grant all on move2alf.* to 'move2alf'@'localhost' identified by 'move2alf';
grant all on move2alf.* to 'move2alf'@'%' identified by 'move2alf';
grant all on move2alf_test.* to 'move2alf'@'localhost' identified by 'move2alf';
grant all on move2alf_test.* to 'move2alf'@'%' identified by 'move2alf';

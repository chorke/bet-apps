delete from scores where matchid in (select id from matches where matchdate >='25-01-2015');
delete from bet1x2 where matchid in (select id from matches where matchdate >='25-01-2015');
delete from betbtts where matchid in (select id from matches where matchdate >='25-01-2015');
delete from betdc where matchid in (select id from matches where matchdate >='25-01-2015');
delete from betdnb where matchid in (select id from matches where matchdate >='25-01-2015');
delete from betou where matchid in (select id from matches where matchdate >='25-01-2015');
delete from betah where matchid in (select id from matches where matchdate >='25-01-2015');
delete from matches where matchdate >='25-01-2015';

-- Extension: postgis
-- DROP EXTENSION postgis;
 CREATE EXTENSION postgis
  SCHEMA public
  VERSION "2.0.6";

-- Schema: topology
-- DROP SCHEMA topology;
CREATE SCHEMA topology
  AUTHORIZATION postgres;

-- Extension: postgis_topology
-- DROP EXTENSION postgis_topology;
 CREATE EXTENSION postgis_topology
  SCHEMA topology
  VERSION "2.0.6";
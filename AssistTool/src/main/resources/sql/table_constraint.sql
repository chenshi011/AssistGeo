select conrelid::regclass AS table_from, conname, pg_get_constraintdef(c.oid)
from   pg_constraint c
join   pg_namespace n ON n.oid = c.connamespace
where  contype in ('f', 'p','c','u') and c.conrelid='camera'::regclass order by contype
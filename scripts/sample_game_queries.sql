select
	pl.name,
	pgs.week,
	pgs.recs,
	pgs.tgts,
	pgs.recyds,
	pgs.rectds
from playergamestats as pgs
inner join players as pl on pgs.playerid = pl.id
order by pgs.tgts desc
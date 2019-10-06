-- SPDX-License-Identifier: Apache-2.0
--
-- generate all ApplicationComponents in denormalized form including all hierachical data (allden -> "all denormalized")
select 
    CONCAT(a."applicationName",ac."groupId",ac."artifactId",ac."version") as CORR_KEY_0,
    CONCAT(a."applicationName",ac."groupId",ac."artifactId") as CORR_KEY_1,
	e.*, 
	a.*, 
	ac.*
from 
	ENGAGEMENT e, 
	APPLICATION a, 
	APPLICATIONCOMPONENT ac 
where 
	e.ID_ENGAGEMENT = a.PARENT_APPLICATION AND 
	a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT
order by
	UPPER("ID_APPLICATION"), -- sort by ID so assuring we have the same order as defined in config
	UPPER("groupId"), 
	UPPER("artifactId"),
	UPPER("version")
	
SELECT
    ConceptName AS concept,
    coalesce(RankLevel, '') || coalesce(RankName, '') AS conceptrank
FROM
    Concept c RIGHT JOIN
    ConceptName n ON n.ConceptID_FK = c.id
WHERE
    ConceptName = ?
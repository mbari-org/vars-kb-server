SELECT
    ConceptName AS concept,
    ISNULL(RankLevel, '') + ISNULL(RankName, '') AS rank
FROM
    Concept AS c RIGHT JOIN
    ConceptName AS n ON n.ConceptID_FK = c.id
WHERE
    ConceptName = ?
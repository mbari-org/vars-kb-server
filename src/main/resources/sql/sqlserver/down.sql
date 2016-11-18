WITH org_name AS
    (
        SELECT DISTINCT
            parent.id AS parent_id,
            parentname.ConceptName as parent_name,
            ISNULL(parent.RankLevel, '') + ISNULL(parent.RankName, '') AS parent_rank,
            child.id AS child_id,
            childname.ConceptName as child_name,
            ISNULL(child.RankLevel, '') + ISNULL(child.RankName, '') AS child_rank
        FROM
            Concept parent RIGHT OUTER JOIN
            Concept child ON child.ParentConceptID_FK = parent.id LEFT OUTER JOIN
            ConceptName childname ON childname.ConceptID_FK = child.id LEFT OUTER JOIN
            ConceptName parentname ON parentname.ConceptID_FK = parent.id
        WHERE
            childname.NameType = 'Primary' AND
            parentname.NameType = 'Primary'
    ),
    jn AS
    (
        SELECT
            parent_id,
            parent_name,
            parent_rank,
            child_id,
            child_name,
            child_rank
        FROM
            org_name
        WHERE
            parent_name = ?
        UNION ALL
            SELECT
                C.parent_id,
                C.parent_name,
                C.parent_rank,
                C.child_id,
                C.child_name,
                C.child_rank
            FROM
                jn AS p JOIN
                org_name AS C ON C.parent_id = p.child_id
    )
SELECT DISTINCT
    jn.parent_id,
    jn.parent_name,
    jn.parent_rank,
    jn.child_id,
    jn.child_name,
    jn.child_rank
FROM
    jn
ORDER BY
    1;
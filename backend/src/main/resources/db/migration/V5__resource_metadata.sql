-- Adds real filterable metadata to resources, ahead of digitizing a large batch of
-- worksheets (grade/subject/year) — cheaper to add now than to re-tag hundreds of
-- rows later. All nullable: existing resources and non-worksheet content (articles,
-- courses) don't necessarily have a single grade/subject/year.

ALTER TABLE resources ADD COLUMN grade INTEGER;
ALTER TABLE resources ADD COLUMN subject VARCHAR(255);
ALTER TABLE resources ADD COLUMN source_year INTEGER;

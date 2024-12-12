drop index if exists indexer_exp.github_labels_name_index;

create unique index on indexer_exp.github_labels (id, name);
create unique index on indexer_exp.github_labels (name, id);

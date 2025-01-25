-- Créer la table si elle n'existe pas
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    parent_category_id INTEGER REFERENCES categories(id)
);


-- Remplir la table avec des données de test
INSERT INTO categories (id, name, description, parent_category_id) VALUES
(1, 'Catégorie principale 1', 'Description de la catégorie 1', NULL),
(2, 'Catégorie principale 2', 'Description de la catégorie 2', NULL),
(3, 'Sous-catégorie 1', 'Description de la sous-catégorie 1', 1),
(4, 'Sous-catégorie 2', 'Description de la sous-catégorie 2', 1),
(5, 'Sous-catégorie 3', 'Description de la sous-catégorie 3', 2);

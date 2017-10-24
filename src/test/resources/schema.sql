CREATE TABLE jobs ( 
   id INT NOT NULL, 
   external_uri VARCHAR(50) NOT NULL, 
   service VARCHAR(20),
   status VARCHAR(20) NOT NULL, 
   type VARCHAR(20) NOT NULL, 
);
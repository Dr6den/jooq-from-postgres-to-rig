1. run mvn clean install that will create executable jar file in target folder of the project with name
migration-rigagentdb-from-postgres-to-derby
2. create config.properties file with credentials to postgre source database and derby target database using this pattern:

db.from.driver=org.postgresql.Driver
db.from.url=jdbc:postgresql://localhost:9432/nectar?currentSchema=nectarcorp_rigagentdb
db.from.username=nectar
db.from.password=ie0CBypXQz66
db.from.schema=nectarcorp_rigagentdb

db.to.driver=org.apache.derby.jdbc.ClientDriver
db.to.url=jdbc:derby://localhost:1527/nectar
db.to.username=nectar
db.to.password=ie0CBypXQz66
db.to.schema=nectarcorp_rigagentdb

3. execute jar in a way java -jar (nameofjar)

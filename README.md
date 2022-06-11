# Ordina OpenSky assignment
Run the java file located in `src/main/java/com/ordina/App.java`.

### Dashboard
Navigate to [http://localhost:8080/](http://localhost:8080/) to show the front-end showing all statistics.

### API endpoints:
- [/count](http://localhost:8080/count) returns JSON data of top 3 countries of origin.
- [/altitude](http://localhost:8080/altitude) returns JSON data of plane identifiers grouped by altitude slices.
- [/warnings](http://localhost:8080/warnings) returns list of which planes are likely to change altitude slice before next data is received.
- [/nl](http://localhost:8080/nl) returns the number of planes above the Netherlands per hour.
- [/all](http://localhost:8080/all) shows all data combined in a single JSON.

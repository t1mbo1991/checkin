![Build](https://github.com/hhu-propra2/praktikum-gruppe-26/actions/workflows/gradle-ci.yml/badge.svg)
# praktikum-gruppe-26

## Planung
[Notion](https://www.notion.so/Propra-2-Praktikum-861058da0f5d4ea3b13b0b280ea7ce40)

## Dokumentation

Die Dokumentation ist hier unter [Dokumentation](documentation.adoc) zu finden.

## Anleitung

### Anwendung konfigurieren

Um die Anwendung zu konfigurieren, werden in der Konfigurationsdatei "application.yaml"
der Zeitrahmen des Praktikums, die Menge an Urlaub eines einzelnen Studenten in Minuten und die
Rollen (admins und leader) gesetzt.

Diese Datei ist unter folgendem Dateipfad zu finden:

    ./chicken/chicken_spring/src/main/resources/application.yaml

Um den Praktikumszeitrahmen z.B. vom 07.03.2022 bis zum 25.03.2022 von 
täglich 9:30 bis 13:30 mit einer Urlaubszeit von 4 Stunden (240 Minuten) pro Student für den gesamten Zeitraum zu setzen, schreiben wir:

    config:
        maxHolidays:  240
        startDate:    "2022-03-07"
        endDate:      "2022-03-25"
        startTime:    "09:30"
        endTime:      "13:30"

Um z.B. den Nutzern mit den GitHubhandles "checkinBreast" und "checkinLegs" die Rolle "admin" und 
"Huhn12" und "Huhn42" die Rolle "leader" zuzuweisen, schreiben wir:

    roles:
        admins: checkinBreast checkinLegs
        leaders: Huhn12 Huhn42


Die client-id und das client-secret übergeben wir beim Start mit den Variablen

    CLIENT_ID

und

    ClIENT_SECRET


### Anwendung starten

WICHTIG: Um die Anwendung zu starten, muss Docker installiert und an sein.

Nachdem wir die Anwendung konfiguriert haben, können wir sie starten.

Dafür starten/verbinden wir uns zunächst mit der Datenbank mit dem Befehl:

    docker-compose up

Nun starten wir die Anwendung mit dem Befehl:

    gradle bootRun --args="--CLIENT_ID={client id} --CLIENT_SECRET={client secret}"

{client id} und {client secret} sind hier unsere Umgebungsvariablen, die wir setzen müssen.


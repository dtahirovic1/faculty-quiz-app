# RMA

Mobilna aplikacija za fakultetske kvizove rađena u okviru predmeta **Razvoj mobilnih aplikacija**.

## Tehnologije

- Kotlin
- Jetpack Compose
- Android Studio

# Spirala 4:

## Korisnička perspektiva:

### 1. Početni ekran za unos username-a

Dodana je nova početna stranica `AccountScreen` na kojoj korisnik unosi username. Uneseni username se u pozadini koristi kao hash za rad sa backend servisom.

### 2. Kviz se više ne pokreće automatski otvaranjem detalja

U originalnoj verziji ulazak u detalje kviza odmah je započinjao pokušaj kviza. Sada ekran prvo prikazuje detalje kviza, a pokušaj se kreira tek klikom na dugme `Započni kviz`.

### 3. Dodano dugme `Završi kviz`

Dodano je dugme kojim korisnik jasno završava kviz. Prije završavanja provjerava se da li su odgovorena sva pitanja.

### 4. Rezultat se ne prikazuje tokom rada kviza

Uklonjen je prikaz rezultata dok korisnik odgovara na pitanja. Odgovori se pamte lokalno i šalju se tek kada korisnik klikne `Završi kviz`.

### 5. Blokirano pokretanje kviza ako korisnik nije upisan u odgovarajuću grupu i predmet

Aplikacija sada provjerava da li je korisnik upisan baš u grupu kojoj kviz pripada.


## Razvojna perspektiva:

### 1. Izdvojena logika filtriranja kvizova

Logika za filtriranje kvizova izdvojena je iz `KvizViewModel` u novu pomoćnu klasu `KvizFilter`.

### 2. Izdvojeno računanje statusa kviza

Računanje statusa kviza izdvojeno je u `KvizStatusCalculator`.

### 3. Centralizovane tekstualne poruke

Poruke za greške, statuse i korisničke obavijesti prebačene su u `Messages`.

### 4. Lokalno zaključavanje završenog kviza

Dodana je lokalna evidencija završenih kvizova u `TakeKvizRepository`, tako da se završeni kviz tretira kao zaključen.


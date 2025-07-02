package de.hs_kl.libris.util

import de.hs_kl.libris.data.model.Book
import de.hs_kl.libris.data.model.ReadingStatus
import java.text.SimpleDateFormat
import java.util.Calendar

object TestData {
    fun getTestBooks(): List<Book> = listOf(
        Book(
            id = "XZWYP8dTloYC",
            title = "Das C++ Kompendium",
            author = "Gilbert Brands",
            isbn = "9783642047879",
            pageCount = 828,
            currentPage = 270,
            coverUrl = "http://books.google.com/books/content?id=XZWYP8dTloYC&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.COMPLETED,
            startDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2024-04-07T19:40:50")
            }.time,
            completionDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2024-12-29T19:40:50")
            }.time,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T19:40:50"),
            publisher = "Springer-Verlag",
            publishedDate = "2010-08-09",
            description = "Dieses Lehrbuch vermittelt sehr detailliert die zentralen Konzepte der Programmierung in C++. Dabei wird anhand vielfältiger, komplexer Problemstellungen die Entwicklung korrekten und wiederverwendbaren Codes gezeigt und zudem eine Programmiertechnik vorgestellt, die typische Fehler und Inkonsistenzen zu vermeiden hilft. In den einzelnen Kapiteln werden Grundlagen und fortgeschrittene Themen zu fast allen Gebieten der Programmierung unter C++ betrachtet. Der Leser wird schrittweise anhand praktischer Aufgaben an die Problemstellungen herangeführt. Besonderer Wert wird auf den Einsatz der Template-Technik gelegt, die viele kritische Aufgaben der Codeerzeugung an den Compiler delegiert und anwendungsbezogen optimal-effizienten Code erzeugt. Die mathematische und algorithmische Herangehensweise machen das Buch auch zu einem wertvollen Studiumsbegleiter in Veranstaltungen wie \"Algorithmen und Datenstrukturen\", \"numerische Mathematik\" und vielen weiteren.",
            categories = listOf("Technology", "Programming", "Computer Science"),
            language = "German",
            apiId = "XZWYP8dTloYC",
            apiSource = "Google Books"
        ),
        Book(
            id = "HRM4kMDHKtsC",
            title = "Java: nebenläufige & verteilte Programmierung",
            author = "Peter Ziesche, Doga Arinir",
            isbn = "9783868340150",
            pageCount = 378,
            currentPage = 209,
            coverUrl = "http://books.google.com/books/content?id=HRM4kMDHKtsC&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.NOT_STARTED,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T19:40:51"),
            publisher = "W3l GmbH",
            publishedDate = "2010",
            description = "Lorem ipsum dolor sit amet...",
            categories = listOf("Technology", "Programming", "Java"),
            language = "German",
            apiId = "HRM4kMDHKtsC",
            apiSource = "Google Books"
        ),

        Book(
            id = "nyC_DwAAQBAJ",
            title = "Kotlin",
            author = "Karl Szwillus",
            isbn = "9783958458550",
            pageCount = 440,
            currentPage = 172,
            coverUrl = "http://books.google.com/books/content?id=nyC_DwAAQBAJ&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.ON_HOLD,
            startDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2024-04-13T19:40:51")
            }.time,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T19:40:51"),
            publisher = "MITP-Verlags GmbH & Co. KG",
            publishedDate = "2019-11-21",
            description = "Fundierte Einführung mit zahlreichen Beispielen aus der Praxis Kotlin für Android- und Webanwendungen Mit vielen Tipps für Java-Umsteiger Kotlin ist eine Programmiersprache, die sich in den letzten Jahren von einem reinen Java-Ersatz für Android zu einer vollwertigen Cross-Plattform-Sprache entwickelt hat. Dieses Buch richtet sich an Entwickler, die Kotlin als neue Programmiersprache kennenlernen und in einer Java-Umgebung wie Android einsetzen wollen, oder die sich für Multiplattform-Techniken interessieren. Dabei konzentriert sich der Autor auf die Grundlagen der Sprache und erläutert umfassend ihre Strukturen, Befehle und Sprachfeatures. Anhand zahlreicher Beispiele lernen Sie, wie Sie Kotlin in einer Produktivumgebung effektiv einsetzen können. Da Kotlin funktionale Programmierung ermöglicht und sich an diesem Konzept orientiert, erläutert der Autor außerdem, was Sie wissen müssen, um funktionalen und objektorientierten Stil zu kombinieren. Darüber hinaus erhalten Sie einen Ausblick auf weiterführende Themen und Konzepte wie automatische Tests, die Organisation von größeren Projekten durch Architekturmuster und die Nebenläufigkeit mit Kotlin-Coroutines. Auch die Anwendung von Kotlin für Android wird vorgestellt und gezeigt, wie die neue Sprache konkret hilft, moderne Architekturen umzusetzen. Zum Abschluss geht der Autor auf die Entwicklung von Cross-Plattform- sowie JavaScript-Anwendungen mit Kotlin ein. Mit diesem Buch erhalten Sie einen umfassenden Einstieg in Kotlin. Es enthält viele Informationen für Entwickler, die sich das erste Mal mit einer statisch typisierten Sprache beschäftigen und für diejenigen, die von der Android-Entwicklung mit Java kommen und auf Kotlin umsteigen und bisherigen Code ergänzen oder ersetzen wollen.",
            categories = listOf("Computers"),
            language = "German",
            apiId = "nyC_DwAAQBAJ",
            apiSource = "Google Books"
        ),
        Book(
            id = "MjHbEAAAQBAJ",
            title = "Programmieren lernen mit Kotlin",
            author = "Christian Kohls, Alexander Dobrynin",
            isbn = "9783446478497",
            pageCount = 568,
            currentPage = 388,
            coverUrl = "http://books.google.com/books/content?id=MjHbEAAAQBAJ&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.IN_PROGRESS,
            startDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2024-05-22T19:40:51")
            }.time,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T19:40:51"),
            publisher = "Carl Hanser Verlag GmbH  Co KG",
            publishedDate = "2023-10-09",
            description = "Fundierter Einstieg in die objektorientierte Programmierung mit Kotlin - Zahlreiche Praxisbeispiele, Erklärbilder und anschauliche Alltagsmetaphern - Durchstarten ohne Vorkenntnisse und eigene Apps entwickeln - Vermittelt Hintergrundwissen und wie man guten Code gestaltet - Quellcode und Zusatzmaterial unter plus.hanser-fachbuch.de - Ihr exklusiver Vorteil: E-Book inside beim Kauf des gedruckten Buches Steigen Sie ein in die funktionale und objektorientierte Programmierung mit Kotlin. Das Buch richtet sich an Studierende und Quereinsteiger, die erstmalig eine Programmiersprache lernen. Kotlin eignet sich sehr gut als Anfängersprache: Erste Erfolge werden schnell erzielt und der Code ist kurz, präzise, leicht verständlich und robust. Gleichzeitig erlaubt Kotlin die professionelle Entwicklung und die Umsetzung umfangreicher Software-Architekturen. Das Buch erklärt anschaulich die Grundlagen des Programmierens, z. B. Variablen, Ausdrücke, Kontrollstrukturen und Funktionen. Objektorientierte Konzepte wie Abstraktion, Vererbung, Polymorphie, Kapselung und Komposition werden anhand von praktischen Beispielen eingeführt. In den vertiefenden Abschnitten lernen Sie Android-Apps umzusetzen, Algorithmen und Datenstrukturen selber zu implementieren, z. B. verkettete Listen, und das Entwickeln mit Coroutinen. Anhand eines durchgehenden Beispiels entwickeln Sie ein Simulationsspiel für Android.",
            categories = listOf("Computers"),
            language = "German",
            apiId = "MjHbEAAAQBAJ",
            apiSource = "Google Books"
        ),

        Book(
            id = "7rp5EAAAQBAJ",
            title = "Der Untergang von Númenor und andere Geschichten aus dem Zweiten Zeitalter von Mittelerde",
            author = "J.R.R. Tolkien",
            isbn = "9783608119855",
            pageCount = 450,
            currentPage = 123,
            coverUrl = "http://books.google.com/books/content?id=7rp5EAAAQBAJ&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.COMPLETED,
            startDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2024-03-14T19:40:52")
            }.time,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T19:40:52"),
            publisher = "Klett-Cotta",
            publishedDate = "2022-11-10",
            description = "SPIEGEL-Bestseller Alle Geschichten und Schriften J.R.R. Tolkiens zum Zweiten Zeitalter von Mittelerde erstmals in einem Band! Eine Geschichte über Elben und Menschen und eine Macht, die böser ist als alles andere. Die große Verführung durch Ringe, die im Geheimen geschmiedet wurden. Die Sehnsucht nach Unsterblichkeit, die in den Untergang führt. Eine Insel aus längst vergangenen Tagen, die zwischen Mittelerde und dem Reich der göttlichen Valar liegt. »Der Untergang von Númenor« versammelt alle wichtigen Originaltexte Tolkiens, die sich mit dem Zweiten Zeitalter beschäftigen und damit die Vorgeschichte des »Herr der Ringe« erzählen. Die Geschichten rund um Elben, Menschen und die Ringe der Macht bilden den Handlungsrahmen, in dem die gigantische Serienverfilmung angesiedelt ist. Mit zahlreichen Bildern und Zeichnungen von Alan Lee Der Herausgeber Brian Sibley: »Seit der Erstveröffentlichung von ›Das Silmarillion‹ vor fünfundvierzig Jahren habe ich Christopher Tolkiens akribische wissenschaftliche Aufarbeitung der Schriften seines Vaters über Mittelerde mit Bewunderung verfolgt. Es ist mir eine Ehre, dieses grundlegende Werk mit ›Der Untergang von Númenor‹ zu ergänzen. Ich hoffe, dass die Leserinnen und Leser durch die Zusammenführung vieler Fäden aus den Erzählungen des Zweiten Zeitalters in einem einzigen Werk das reiche Tableau von Charakteren und Ereignissen entdecken – oder wiederentdecken –, das den Auftakt zum Drama des großen Ringkriegs bildet, wie es in ›Der Herr der Ringe‹ erzählt wird.« Der Illustrator Alan Lee: »Es ist eine große Freude, das Zweite Zeitalter genauer erkunden zu können und mehr über die schattenhaften und uralten Ereignisse, Bündnisse und Katastrophen zu erfahren, die schließlich in die besser bekannten Geschichten des Dritten Zeitalters münden. Wo immer ich bei der Arbeit an ›Der Herr der Ringe‹ und ›Der Hobbit‹ die Gelegenheit hatte, habe ich versucht, in den Bildern und Entwürfen jene historische Tiefe auszuloten, in deren Schichten sich Anklänge an diese älteren Geschehnisse finden. ›Der Untergang von Númenor‹ hat sich als perfekte Gelegenheit erwiesen, ein wenig tiefer in die reiche Geschichte von Mittelerde einzutauchen.«",
            categories = listOf("Fiction", "Fantasy", "High Fantasy"),
            language = "German",
            apiId = "7rp5EAAAQBAJ",
            apiSource = "Google Books"
        ),
        Book(
            id = "st-PDQAAQBAJ",
            title = "Per Anhalter durch die Galaxis",
            author = "Douglas Adams",
            isbn = "9783036993577",
            pageCount = 182,
            currentPage = 91,
            coverUrl = "http://books.google.com/books/content?id=st-PDQAAQBAJ&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.NOT_STARTED,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T19:59:08"),
            publisher = "Kein & Aber AG",
            publishedDate = "2017-04-26",
            description = "E s ist nicht sein Tag. Erst wird sein Haus abgerissen und dann von einer Flotte hässlicher Vogonen gleich die ganze Erde gesprengt. Doch für Arthur Dent beginnt die wohl unterhaltsamste und abenteuerlichste Reise der Menschheitsgeschichte – per Anhalter durch die Galaxis.",
            categories = listOf("Fiction", "Science Fiction", "Humor"),
            language = "German",
            apiId = "st-PDQAAQBAJ",
            apiSource = "Google Books"
        ),
        Book(
            id = "IHqGBByU0sgC",
            title = "Harry Potter und der Gefangene von Askaban",
            author = "J.K. Rowling",
            isbn = "9781781100783",
            pageCount = 494,
            currentPage = 358,
            coverUrl = "http://books.google.com/books/content?id=IHqGBByU0sgC&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.NOT_STARTED,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T19:59:09"),
            publisher = "Pottermore Publishing",
            publishedDate = "2015-12-08",
            description = "Natürlich weiß Harry, dass das Zaubern in den Ferien strengstens verboten ist, und trotzdem befördert er seine schreckliche Tante mit einem Schwebezauber an die Decke. Die Konsequenz ist normalerweise: Schulverweis! Nicht so bei Harry; im Gegenteil, man behandelt ihn wie in rohes Ei. Hat es etwa damit zu tun, dass ein gefürchteter Verbrecher in die Schule eingedrungen ist und es auf Harry abgesehen hat? Mit seinen Freunden Ron und Hermine versucht Harry ein Geflecht aus Verrat, Rache, Feigheit und Verleumdung aufzudröseln und stößt dabei auf Dinge, die ihn fast an seinem Verstand zweifeln lassen.",
            categories = listOf("Fiction", "Fantasy", "Young Adult"),
            language = "German",
            apiId = "IHqGBByU0sgC",
            apiSource = "Google Books"
        ),
        Book(
            id = "bHp5CK4DLmIC",
            title = "Harry Potter und die Kammer des Schreckens",
            author = "J.K. Rowling",
            isbn = "9781781100776",
            pageCount = 394,
            currentPage = 356,
            coverUrl = "http://books.google.com/books/content?id=bHp5CK4DLmIC&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.COMPLETED,
            startDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2024-05-12T19:59:09")
            }.time,
            completionDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-15T19:59:09")
            }.time,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T19:59:09"),
            publisher = "Pottermore Publishing",
            publishedDate = "2015-12-08",
            description = "Endlich wieder Schule!!! Einen solchen Seufzer kann nur der ausstoßen, dessen Ferien scheußlich und die Erinnerung an das vergangene Schuljahr wunderbar waren: Harry Potter. Doch wie im Vorjahr stehen nicht nur Zaubertrankunterricht und Verwandlung auf dem Programm. Ein grauenhaftes Etwas treibt sein Unwesen in den Gemäuern der Schule - ein Ungeheuer, für das nicht einmal die mächtigsten Zauberer eine Erklärung finden. Wird Harry mit Hilfe seiner Freunde Ron und Hermine das Rätsel lösen und Hogwarts von den dunklen Mächten befreien können?",
            categories = listOf("Fiction", "Fantasy", "Young Adult"),
            language = "German",
            apiId = "bHp5CK4DLmIC",
            apiSource = "Google Books"
        ),
        Book(
            id = "XtekEncdTZcC",
            title = "Harry Potter und der Stein der Weisen",
            author = "J.K. Rowling",
            isbn = "9781781100769",
            pageCount = 359,
            currentPage = 60,
            coverUrl = "http://books.google.com/books/content?id=XtekEncdTZcC&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.IN_PROGRESS,
            startDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2024-07-19T19:59:09")
            }.time,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T19:59:09"),
            publisher = "Pottermore Publishing",
            publishedDate = "2015-12-08",
            description = "Eigentlich hatte Harry geglaubt, er sei ein ganz normaler Junge. Zumindest bis zu seinem elften Geburtstag. Da erfährt er, dass er sich an der Schule für Hexerei und Zauberei einfinden soll. Und warum? Weil Harry ein Zauberer ist. Und so wird für Harry das erste Jahr in der Schule das spannendste, aufregendste und lustigste in seinem Leben. Er stürzt von einem Abenteuer in die nächste ungeheuerliche Geschichte, muss gegen Bestien, Mitschüler und Fabelwesen kämpfen. Da ist es gut, dass er schon Freunde gefunden hat, die ihm im Kampf gegen die dunklen Mächte zur Seite stehen.",
            categories = listOf("Fiction", "Fantasy", "Young Adult"),
            language = "German",
            apiId = "XtekEncdTZcC",
            apiSource = "Google Books"
        ),
        Book(
            id = "pD6arNyKyi8C",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            isbn = "9780547951973",
            pageCount = 331,
            currentPage = 236,
            coverUrl = "http://books.google.com/books/content?id=pD6arNyKyi8C&printsec=frontcover&img=1&zoom=10&source=gbs_api",
            status = ReadingStatus.ON_HOLD,
            startDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2024-04-11T19:59:10")
            }.time,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T19:59:10"),
            publisher = "HarperCollins",
            publishedDate = "2012-02-15",
            description = "J.R.R. Tolkien\'s classic prelude to his Lord of the Rings trilogy... Bilbo Baggins is a hobbit who enjoys a comfortable, unambitious life, rarely traveling any farther than his pantry or cellar. But his contentment is disturbed when the wizard Gandalf and a company of dwarves arrive on his doorstep one day to whisk him away on an adventure. They have launched a plot to raid the treasure hoard guarded by Smaug the Magnificent, a large and very dangerous dragon. Bilbo reluctantly joins their quest, unaware that on his journey to the Lonely Mountain he will encounter both a magic ring and a frightening creature known as Gollum. Written for J.R.R. Tolkien\'s own children, The Hobbit has sold many millions of copies worldwide and established itself as a modern classic.",
            categories = listOf("Fiction", "Fantasy", "Adventure"),
            language = "English",
            apiId = "pD6arNyKyi8C",
            apiSource = "Google Books"
        ),
        Book(
            id = "ASDy1OLaP1cC",
            title = "Sakrileg - The Da Vinci Code",
            author = "Dan Brown",
            isbn = "9783838705910",
            pageCount = 665,
            currentPage = 17,
            coverUrl = "http://books.google.com/books/content?id=ASDy1OLaP1cC&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.ON_HOLD,
            startDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2024-03-22T19:59:16")
            }.time,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T19:59:16"),
            publisher = "BASTEI LÜBBE",
            publishedDate = "2010-10-29",
            description = "Auf der Suche nach dem Da Vinci Code ... Robert Langdon, Symbolologe aus Harvard, befindet sich aus beruflichen Gründen in Paris, als er einen merkwürdigen Anruf erhält: Der Chefkurator des Louvre wurde mitten in der Nacht vor dem Gemälde der Mona Lisa ermordet aufgefunden. Langdon begibt sich zum Tatort und erkennt schon bald, dass der Tote durch eine Reihe von versteckten Hinweisen auf die Werke Leonardo da Vincis aufmerksam machen wollte - Hinweise, die seinen gewaltsamen Tod erklären und auf eine finstere Verschwörung deuten. Bei seiner Suche nach den Hintergründen der Tat wird Robert Langdon von Sophie Neveu unterstützt, einer Kryptologin der Pariser Polizei und Enkeltochter des ermordeten Kurators. Eine aufregende Jagd beginnt ... Mit dem Thriller Sakrileg schrieb Dan Brown einen Mega-Bestseller, der mit Tom Hanks in der Hauptrolle ein großer Kinoerfolg wurde. Dieses E-Book enthält zusätzlich eine Leseprobe von Dan Browns Roman Inferno.",
            categories = listOf("Fiction", "Thriller", "Mystery"),
            language = "German",
            apiId = "ASDy1OLaP1cC",
            apiSource = "Google Books"
        ),
        Book(
            id = "dcXkEAAAQBAJ",
            title = "Albert Camus oder der glückliche Sisyphos – Albert Camus ou Sisyphe heureux",
            author = "Willi Jung",
            isbn = "9783847001461",
            pageCount = 461,
            currentPage = 44,
            coverUrl = "http://books.google.com/books/content?id=dcXkEAAAQBAJ&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.COMPLETED,
            startDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2024-11-18T20:04:14")
            }.time,
            completionDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-23T20:04:14")
            }.time,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T20:04:14"),
            publisher = "V&R Unipress",
            publishedDate = "2013-08-14",
            description = "Through his literary, philosophical and contemporary critical works, Albert Camus counts among the greatest French intellectuals of the 20th century. Although Camus still reaches a large number of readers today, there is no extensive institutionalised Camus research at universities. This anthology is published within the temporal context of his 100th birthday and the 50th anniversary of his death. As a representative of a 20th-century generation, Camus seems to have become a mythological figure, who understood historic and individual catastrophes to be personal challenges and firmly believed that humanitarianism will prevail.The volume is divided into three main chapters. The first chapter brings together treatises on philosophical, ethical and political questions under the heading Commitment; the second, under the heading Aesthetics, his works on narratives and drama; the third, finally, is dedicated to the relation between tradition and modernity and deals with questions of reception and intertextuality.",
            categories = listOf("Literary Criticism", "Philosophy"),
            language = "German",
            apiId = "dcXkEAAAQBAJ",
            apiSource = "Google Books"
        ),
        Book(
            id = "NJADEQAAQBAJ",
            title = "Real Osamu Dazai",
            author = "Osamu Dazai",
            isbn = "9781462925070",
            pageCount = 345,
            currentPage = 122,
            coverUrl = "http://books.google.com/books/content?id=NJADEQAAQBAJ&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.DROPPED,
            startDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2024-02-08T20:04:14")
            }.time,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T20:04:14"),
            publisher = "Tuttle Publishing",
            publishedDate = "2024-09-24",
            description = "\"Dazai\'s brand of egoistic pessimism dovetails organically with the emo chic of this cultural moment and with the inner lives of teenagers of all eras.\" —Andrew Martin, The New York Times Best-known for his novels No Longer Human and The Setting Sun, Dazai was also an acclaimed writer of short stories, experimenting with a wide variety of styles and bringing to each work a sophisticated sense of humor, a broad empathy for the human condition and a tremendous literary talent. The twenty stories in this collection include: Memories — An autobiographical tale in which Dazai relates episodes from his own childhood and adolescence, showing his relationship with his family and his tendency towards introspection and self-dramatization On the Question of Apparel — A comic tour-de-force in which Dazai examines the hold that fashion has over him and how it relates to his own pathetic self-image A Poor Man\'s Got His Pride — A retelling of a story by 18th-century master of burlesque fiction Ihara Saikaku, about a fallen samurai who lives in poverty The Sound of Hammering — A love story set against the backdrop of the rebuilding of Tokyo after the city was totally destroyed during World War Two And sixteen other stories! By turns hilarious, ironic, introspective, mystical and sarcastic, these stories present a fully rounded portrait of a talented writer who tried several times to take his own life and ultimately succeeded. An introduction by translator James O\'Brien gives the background to Dazai\'s life and shows how the stories in this book, whether autobiographical or fictional, contribute to an understanding of one of Japan\'s greatest writers. **Recommended for readers 16 years & up. Not intended for high school classroom use due to adult content.**",
            categories = listOf("Fiction", "Literary Fiction"),
            language = "English",
            apiId = "NJADEQAAQBAJ",
            apiSource = "Google Books"
        ),
        Book(
            id = "hC3pEAAAQBAJ",
            title = "Nah bei dir, Band 09",
            author = "Karuho Shiina",
            isbn = "9783842095335",
            pageCount = 179,
            currentPage = 130,
            coverUrl = "http://books.google.com/books/content?id=hC3pEAAAQBAJ&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.COMPLETED,
            startDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2024-09-15T20:04:13")
            }.time,
            completionDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-19T20:04:13")
            }.time,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T20:04:13"),
            publisher = "TOKYOPOP Verlag",
            publishedDate = "2023-12-15",
            description = "Kent kann sich nicht vorstellen, dass ein so umschwärmter Junge wie Kazehaya an einer Außenseiterin wie Sawako interessiert sein kann. Aus der guten Absicht heraus, sie vor Liebeskummer zu schützen, sagt er Sawako, dass Kazehaya nur aus Anstand nett zu ihr ist und in Wirklichkeit in eine andere verliebt ist. Sawako bricht daraufhin in Tränen aus, weil Kent damit ihre schlimmsten Ängste bestätigt hat. Und genau in diesem Moment kommt Kazehaya vorbei ...",
            categories = listOf("Comics & Manga", "Romance"),
            language = "German",
            apiId = "hC3pEAAAQBAJ",
            apiSource = "Google Books"
        ),
        Book(
            id = "VnxoEAAAQBAJ",
            title = "Fingerzeige - Intentions",
            author = "Oscar Wilde",
            isbn = "9783753133249",
            pageCount = 201,
            currentPage = 8,
            coverUrl = "http://books.google.com/books/content?id=VnxoEAAAQBAJ&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.IN_PROGRESS,
            startDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2024-08-26T20:04:13")
            }.time,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T20:04:13"),
            publisher = "epubli",
            publishedDate = "2020-12-13",
            description = "Fingerzeige – Intentions: Essays und Dialoge über Ästhetik des britischen Schriftstellern Oscar Wilde. Enthält ›Der Verfall der Lüge‹, ›Stift, Gift, Schrifttum‹, ›Kritik als Kunst – Mit einigen Anmerkungen über die Wichtigkeit des Nichtstuns‹, ›Kritik als Kunst – Mit einigen Anmerkungen über die Wichtigkeit allumfassender Erörterung‹, ›Die Wahrheit der Masken‹.",
            categories = listOf("Literary Criticism", "Art"),
            language = "German",
            apiId = "VnxoEAAAQBAJ",
            apiSource = "Google Books"
        ),
        Book(
            id = "dxXzEAAAQBAJ",
            title = "Franz Kafka",
            author = "Franz Kafka",
            isbn = "9783869926551",
            pageCount = 966,
            currentPage = 935,
            coverUrl = "http://books.google.com/books/content?id=dxXzEAAAQBAJ&printsec=frontcover&img=1&zoom=10&edge=curl&source=gbs_api",
            status = ReadingStatus.IN_PROGRESS,
            startDate = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2024-05-22T20:04:12")
            }.time,
            lastModified = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2025-01-24T20:04:12"),
            publisher = "AtheneMedia-Verlag",
            publishedDate = "2024-04-03",
            description = "Franz Kafka, einer der bedeutendsten Schriftsteller des 20. Jahrhunderts, fasziniert bis heute mit seinen einzigartigen Werken. In diesem Buch tauchen wir tief in das Leben und Schaffen des charismatischen Autors ein und entdecken die verborgenen Geheimnisse hinter seinen berühmten Geschichten. Quasi von seinen frühen Jahren in Prag bis zu seinem tragischen Tod im Jahr 1924 begleiten wir den Schriftsteller auf seiner Reise durch das Leben. Wir erfahren von seinen persönlichen Herausforderungen, seinen Ängsten und Zweifeln, die sich in seinen Werken widerspiegeln. Kafka, der zeitlebens mit seinem Schreiben kämpfte, fand in der Literatur einen Ausweg aus der Realität und schuf eine eigene Welt voller Absurdität und Verzweiflung. In diesem Buch sind nicht nur Kafkas bekannteste Werke wie \"Die Verwandlung\", \"Der Prozess\" und \"Das Schloss\" , sondern noch Vieles mehr; tiefgründigen Themen seiner Geschichten - die Entfremdung des Individuums, die Machtlosigkeit gegenüber einer undurchschaubaren Bürokratie und die Suche nach Identität und Sinn. Doch dieses Buch bietet mehr. Lassen Sie uns einen Blick hinter die Kulissen werfen und lernen Sie den Mann hinter den Geschichten kennen. Wir erfahren von seinen persönlichen Beziehungen, seinen Freundschaften und seinen inneren Kämpfen. Wir entdecken die Einflüsse anderer Schriftsteller und Künstler auf sein Werk und wie er die Literaturlandschaft seiner Zeit prägte; ein Buch für alle, die tiefer in das Leben und Schaffen dieses außergewöhnlichen Schriftstellers eintauchen möchten. Es ist eine Reise in die Tiefen der menschlichen Existenz, eine Erkundung der Abgründe der menschlichen Psyche und eine Hommage an einen der größten Schriftsteller aller Zeiten. Tauchen Sie ein in die Welt von Franz Kafka und lassen Sie sich von seiner einzigartigen Vision fesseln und folgenden Texten: Betrachtung, Ein Damenbrevier, Gespräch mit dem Beter, Gespräch mit dem Betrunkenen, Die Aeroplane in Brescia, Das Urteil, Der Heizer, Die Verwandlung, Vor dem Gesetz, Der Kübelreiter, Der Mord, Ein Landarzt, In der Strafkolonie, Ein Hungerkünstler, Kinder auf der Landstraße, Entlarvung eines Bauernfängers, Der plötzliche Spaziergang, Entschlüsse, Der Ausflug ins Gebirge, Das Unglück des Junggesellen, Der Kaufmann, Zerstreutes Hinausschaun, Der Nachhauseweg, Die Vorüberlaufenden, Der Fahrgast, Kleider, Die Abweisung, Zum Nachdenken für Herrenreiter, Das Gassenfenster, Wunsch, Indianer zu werden, Die Bäume, Unglücklichsein, Der neue Advokat, Ein Landarzt, Auf der Galerie, Ein altes Blatt, Vor dem Gesetz, Schakale und Araber, Ein Besuch im Bergwerk, Das nächste Dorf, Eine kaiserliche Botschaft, Die Sorge des Hausvaters, Elf Söhne, Ein Brudermord, Ein Traum, Ein Bericht für eine Akademie ...",
            categories = listOf("Fiction", "Literary Fiction", "Classics"),
            language = "German",
            apiId = "dxXzEAAAQBAJ",
            apiSource = "Google Books"
        ),
    )
}
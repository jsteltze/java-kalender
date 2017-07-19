/**
 *  java-kalender - Java Calendar for Germany
 *  Copyright (C) 2012  Johannes Steltzer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.jsteltze.calendar.config.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.jsteltze.common.calendar.Date;

/**
 * List of all supported frequent holidays / special days / world,action days / memorial days for Germany.
 * @author Johannes Steltzer
 * 
 */
public enum HolidayConstants {
    
    /** Holidays by law. */
    NEUJAHR      (0x00000001, "Neujahr",                    "", Calendar.JANUARY, 1),
    HL3K         (0x00000002, "Heilige drei Könige",        "In vielen Gebieten Deutschlands ist der Begriff \"Dreikönigsfest\" oder \"Dreikönigstag\" der vorherrschend gebrauchte Name für den 6. Januar.\nDie eigentliche Bezeichnung dieses Festes ist dagegen \"Erscheinung des Herrn\". Die Kirche feiert an diesem Tag das Sichtbarwerden der Göttlichkeit Jesu in der Anbetung durch die Magier, bei seiner Taufe im Jordan und durch das von ihm bei der Hochzeit zu Kana bewirkte Wunder der Verwandlung von Wasser in Wein.\n\nIn Deutschland ist der 6. Januar gesetzlicher Feiertag nur in Baden-Württemberg, Bayern und Sachsen-Anhalt.\n\nQuelle: Wikipedia", Calendar.JANUARY, 6),
    GRDO         (0x00000004, "Gründonnerstag",             "Gründonnerstag (auch Hoher, Heiliger oder Weißer Donnerstag bzw. Palmdonnerstag) ist die deutschsprachige Bezeichnung für den fünften Tag der Karwoche bzw. der Heiligen Woche. An ihm gedenken die Kirchen des letzten Abendmahles Jesu mit den zwölf Aposteln am Vorabend seiner Kreuzigung.\n\nGründonnerstag ist kein gesetzlicher Feiertag in Deutschland.\n\nQuelle: Wikipedia"),
    KARFR        (0x00000008, "Karfreitag",                 "Der Karfreitag (althochdeutsch kara 'Klage', 'Kummer', 'Trauer') ist der Freitag vor Ostern. Christen gedenken an diesem Tag des Kreuzestodes Jesu Christi. In der katholischen Kirche ist er ein strenger Fast- und Abstinenztag.\n\nKarfreitag ist ein gesetzlicher Feiertag in Deutschland.\n\nQuelle: Wikipedia"),
    OSTERMO      (0x00000010, "Ostermontag",                "Der Ostermontag gehört mit dem 26. Dezember und dem Pfingstmontag zu den zweiten Feiertagen der höchsten christlichen Feste.\nEr steht als zweiter Feiertag ganz im Zeichen des Osterfestes. Nach der Verkündigung des Neuen Testaments (NT) wurde Jesus Christus, Sohn Gottes, am dritten Tag nach seiner Kreuzigung von den Toten erweckt und erschien seinen Jüngerinnen und Jüngern in leiblicher Gestalt.\n\nOstermontag ist ein gesetzlicher Feiertag in Deutschland.\n\nQuelle: Wikipedia"),
    TDA          (0x00000020, "Tag der Arbeit",             "Der Erste Mai wird als \"Tag der Arbeit\", \"Tag der Arbeiterbewegung\", \"Internationaler Kampftag der Arbeiterklasse\" oder auch als Maifeiertag bezeichnet. Er ist in Deutschland, Liechtenstein, Österreich, Belgien, Teilen der Schweiz und vielen anderen Staaten ein gesetzlicher Feiertag.\n\nQuelle: Wikipedia", Calendar.MAY, 1),
    CHRHIMMELF   (0x00000040, "Christi Himmelfahrt",        "Christi Himmelfahrt (altgr. \"die Aufnahme des Herrn\", lat. \"Aufstieg des Herrn\"), bezeichnet im christlichen Glauben die Rückkehr Jesu Christi als Sohn Gottes zu seinem Vater in den Himmel. Christi Himmelfahrt wird am 40. Tag des Osterfestkreises, also 39 Tage nach dem Ostersonntag, gefeiert. Deshalb fällt das Fest immer auf einen Donnerstag\n\nChristi Himmelfahrt ist ein gesetzlicher Feiertag in Deutschland.\n\nQuelle: Wikipedia"),
    PFINGSTMO    (0x00000080, "Pfingstmontag",              "Pfingsten (altgr. \"fünfzigster Tag\") ist ein christliches Fest. Am 50. Tag des Osterfestkreises, also 49 Tage nach dem Ostersonntag, wird von den Gläubigen die Entsendung des Heiligen Geistes gefeiert.\n\nPfingstmontag ist ein gesetzlicher Feiertag in Deutschland.\n\nQuelle: Wikipedia"),
    FRONLEICH    (0x00000100, "Fronleichnahm",              "Das Fronleichnamsfest oder Fest des heiligsten Leibes und Blutes Christi ist ein Hochfest im Kirchenjahr der katholischen Kirche, mit dem die leibliche Gegenwart Jesu Christi im Sakrament der Eucharistie gefeiert wird.\n\nIn Deutschland ist Fronleichnam ein gesetzlicher Feiertag in den Ländern Baden-Württemberg, Bayern, Hessen, Nordrhein-Westfalen, Rheinland-Pfalz und im Saarland sowie in einigen Gemeinden mit überwiegend katholischer Bevölkerung in den Ländern Sachsen und Thüringen.\n\nQuelle: Wikipedia"),
    MHIMMELF     (0x00000200, "Mariä Himmelfahrt",          "Mariä Aufnahme in den Himmel, auch Mariä Himmelfahrt oder Vollendung Mariens ist ein Hochfest der römisch-katholischen Kirche am 15. August. Der Glaube an die leibliche Aufnahme Mariens in den Himmel ist seit dem 6. Jahrhundert bezeugt und wurde 1950 von Papst Pius XII. für die römisch-katholische Kirche zum Dogma erhoben.\n\nMariä Himmelfahrt (nicht zu verwechseln mit Christi Himmelfahrt) ist in Saarland ein gesetzlicher Feiertag.\n\nQuelle: Wikipedia", Calendar.AUGUST, 15),
    TDDE         (0x00000400, "Tag der deutschen Einheit",  "Der 3. Oktober wurde als Tag der Deutschen Einheit im Einigungsvertrag 1990 zum gesetzlichen Feiertag in Deutschland bestimmt. Als deutscher Nationalfeiertag erinnert er an die deutsche Wiedervereinigung, die mit dem Wirksamwerden des Beitritts der Deutschen Demokratischen Republik zur Bundesrepublik Deutschland am 3. Oktober 1990 vollendet wurde.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 3), 
    REFORM       (0x00000800, "Reformationstag",            "Der Reformationstag, das Reformationsfest oder auch der Gedenktag der Reformation wird von evangelischen Christen in Deutschland und Österreich am 31. Oktober im Gedenken an die Reformation der Kirche durch Martin Luther gefeiert.\n\nDer Reformationstag ist seit der deutschen Wiedervereinigung gesetzlicher Feiertag in den deutschen Ländern Brandenburg, Mecklenburg-Vorpommern, Sachsen, Sachsen-Anhalt und Thüringen.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 31),
    ALLERH       (0x00001000, "Allerheiligen",              "Allerheiligen ist ein christliches Fest, zu dem aller Heiligen gedacht wird - auch solcher, die nicht heiliggesprochen wurden - sowie der vielen Heiligen, um deren Heiligkeit niemand weiß als Gott.\n\nAllerheiligen ist ein gesetzlicher Feiertag in den katholisch geprägten deutschen Bundesländern Baden-Württemberg, Bayern, Nordrhein-Westfalen, Rheinland-Pfalz und Saarland.\n\nQuelle: Wikipedia", Calendar.NOVEMBER, 1),
    BUBT         (0x00002000, "Buß- und Bettag",            "Der Buß- und Bettag in Deutschland ist ein Feiertag der evangelischen Kirche.  Angesichts von Notständen und Gefahren wurde die ganze Bevölkerung zu Umkehr und Gebet aufgerufen. Seit Ende des 19. Jahrhunderts wird ein allgemeiner Buß- und Bettag am Mittwoch vor dem Ewigkeitssonntag, dem letzten Sonntag des Kirchenjahres, begangen, also elf Tage vor dem ersten Adventssonntag bzw. am Mittwoch vor dem 23. November.\nIm Jahr 1994 wurde beschlossen, den Buß- und Bettag als arbeitsfreien Tag mit Wirkung ab 1995 zu streichen, um die Mehrbelastung für die Arbeitgeber durch die Beiträge zur neu eingeführten Pflegeversicherung durch Mehrarbeit der Arbeitnehmer auszugleichen.\nLediglich im Freistaat Sachsen besteht er bis heute als gesetzlicher Feiertag weiter. Dafür bezahlen in Sachsen abhängig Beschäftigte (nicht jedoch deren Arbeitgeber) einen höheren Beitrag zur Pflegeversicherung als im restlichen Bundesgebiet.\n\nQuelle: Wikipedia"),
    WEIH1        (0x00004000, "1. Weihnachtsfeiertag",      "Weihnachten, auch Weihnacht, Christfest oder Heiliger Christ genannt, ist das Fest der Geburt Jesu Christi. Festtag ist der 25. Dezember, der Christtag, auch Hochfest der Geburt des Herrn, dessen Feierlichkeiten am Vorabend, dem Heiligen Abend, beginnen. Er ist in vielen Staaten ein gesetzlicher Feiertag.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 25),
    WEIH2        (0x00008000, "2. Weihnachtsfeiertag",      "Der 26. Dezember ist als Zweiter Weihnachtsfeiertag oder Stephanstag ein gesetzlicher Feiertag in den meisten europäischen Ländern. Er gehört zu den Feiertagen zwischen den Jahren.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 26),
    
    /** Special holidays. */
    VALENTIN     (0x00000001, "Valentinstag",               "Der Valentinstag am 14. Februar gilt in einigen Ländern als Tag der Liebenden. Das Brauchtum dieses Tages geht auf einen oder mehrere christliche Märtyrer namens Valentinus zurück, die der Überlieferung zufolge das Martyrium durch Enthaupten erlitten haben.\n\nAn Bekanntheit gewann der Valentinstag im deutschen Sprachraum durch den Handel mit Blumen, besonders jedoch durch die intensive Werbung der Blumenhändler und Süßwarenfabrikanten.\n\nQuelle: Wikipedia", Calendar.FEBRUARY, 14),
    ROSENM       (0x00000002, "Rosenmontag",                "Der Rosenmontag wird insbesondere im rheinischen Karneval als Höhepunkt der Karnevalszeit, oft mit dem so genannten Rosenmontagszug, begangen. Er fällt auf Montag vor dem Aschermittwoch; 48 Tage vor dem Ostersonntag. In sogenannten Karnevalshochburgen wie unter anderem Köln, Düsseldorf oder Mainz geben am Rosenmontag als Brauchtumstag viele Arbeitgeber ihren Mitarbeitern zwar frei, ein gesetzlicher Feiertag ist der Rosenmontag aber in keinem Bundesland.\nDer Rosenmontag liegt zwischen dem Karnevalssonntag (Fastnachtssonntag) und dem Faschingsdienstag.\n\nQuelle: Wikipedia"),
    FASCHING     (0x00000004, "Faschingsdienstag",          "Der Fastnachtsdienstag (auch Faschingsdienstag, Karnevalsdienstag, oder Veilchendienstag) ist die Bezeichnung für den letzten der Karnevalstage, den Tag zwischen Rosenmontag und Aschermittwoch. Als letzter Tag vor dem Beginn der Fastenzeit kommt ihm in vielen Regionen eine besondere Bedeutung zu. In einigen Regionen, in denen Karneval gefeiert wird, stellt er den Höhepunkt der Feiertage dar.\nInternational ist dieser Tag auch als Mardi Gras (‚Fetter Dienstag‘) oder Shrove oder Pancake (Tues)day bekannt.\nUm Mitternacht in der Nacht zum Aschermittwoch gibt es zahlreiche Rituale, mit denen die Karnevalsfeierlichkeiten beendet werden. Hierzu gehören die Nubbelverbrennungen, aber auch die symbolische Beerdigung von Karneval, Fastnacht oder Fasching.\n\nQuelle: Wikipedia"),
    ASCHERM      (0x00000008, "Aschermittwoch",             "Der Aschermittwoch stellt in der Westkirche seit dem Pontifikat Gregors des Großen den Beginn der 40-tägigen Fastenzeit dar. Es ist der Tag nach dem Fastnachtsdienstag.\nDie Bezeichnung Aschermittwoch kommt von dem Brauch, in der Heiligen Messe dieses Tages die Asche vom Verbrennen der Palmzweige des Vorjahres zu segnen und die Gläubigen mit einem Kreuz aus dieser Asche zu bezeichnen. Der Empfang des Aschenkreuzes gehört zu den heilswirksamen Zeichen, den Sakramentalien.\n\nQuelle: Wikipedia"),
    FRAUEN       (0x00000010, "Frauentag",                  "Der Internationale Frauentag, Weltfrauentag, Frauenkampftag, Internationaler Frauenkampftag oder Frauentag ist ein Welttag, der am 8. März begangen wird. Er entstand als Initiative sozialistischer Organisationen in der Zeit um den Ersten Weltkrieg im Kampf um die Gleichberechtigung, das Wahlrecht für Frauen und die Emanzipation von Arbeiterinnen. Die Vereinten Nationen erkoren ihn später als Tag der Vereinten Nationen für die Rechte der Frau und den Weltfrieden aus.\n\nQuelle: Wikipedia", Calendar.MARCH, 8),
    PALMS        (0x00000020, "Palmsonntag",                "Der Palmsonntag ist der sechste und letzte Sonntag der Fastenzeit und der Sonntag vor Ostern. Mit dem Palmsonntag beginnt die Karwoche, die in der evangelisch-lutherischen Kirche auch Stille Woche genannt wird. Die Große Woche bzw. Heilige Woche der katholischen und der orthodoxen Tradition umfasst darüber hinaus auch Ostern.\n\nQuelle: Wikipedia"),
    MUTTER       (0x00000040, "Muttertag",                  "Der Muttertag ist ein Tag zu Ehren der Mutter und der Mutterschaft. Er hat sich seit 1914, beginnend in den Vereinigten Staaten, in der westlichen Welt etabliert. Im deutschsprachigen Raum und vielen anderen Ländern wird er am zweiten Sonntag im Mai begangen.\n\nQuelle: Wikipedia"),
    KINDER       (0x00000080, "Kindertag",                  "Der Kindertag, auch Weltkindertag, internationaler Kindertag oder internationaler Tag des Kindes, ist ein in über 145 Staaten der Welt begangener Tag, um auf die besonderen Bedürfnisse der Kinder und speziell auf die Kinderrechte aufmerksam zu machen.\nDie Art seiner Ausrichtung reicht von einem Gedenk- bzw. Ehrentag für Kinder über einen Quasi-Feiertag mit Festen und Geschenken bis zu politischen Pressemitteilungen, Aktionen und Demonstrationen in der Tradition eines Kampftages.\nZiel des Tages sind Themen wie Kinderschutz, Kinderpolitik und vor allem die Kinderrechte in das öffentliche Bewusstsein zu rücken.\n\nEs gibt kein international einheitliches Datum, was historisch begründet ist. In über 40 Staaten wie in China, in den USA (teilweise), vielen mittel- und osteuropäischen Ländern sowie Nachfolgestaaten der Sowjetunion wird am 1. Juni der internationale Kindertag begangen.\n\nQuelle: Wikipedia", Calendar.JUNE, 1),
    HALLOWEEN    (0x00000100, "Halloween",                  "Halloween benennt die Volksbräuche am Abend und in der Nacht vor dem Hochfest Allerheiligen, vom 31. Oktober auf den 1. November. Dieses Brauchtum war ursprünglich vor allem im katholischen Irland verbreitet. Die irischen Einwanderer in den USA pflegten ihre Bräuche in Erinnerung an die Heimat und bauten sie aus.\nSeit den 1990er Jahren verbreiten sich Halloween-Bräuche in US-amerikanischer Ausprägung auch im kontinentalen Europa.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 31),
    MARTIN       (0x00000200, "Martinstag",                 "Der Martinstag am 11. November (in Altbayern und Österreich auch 'Martini') ist im Kirchenjahr das Fest des heiligen Martin von Tours. Das Datum des gebotenen Gedenktags im römischen Generalkalender, das sich auch in orthodoxen Heiligenkalendern, im evangelischen Namenkalender und dem anglikanischen Common Worship findet, ist von der Grablegung des heiligen Martin am 11. November 397 abgeleitet. Der Martinstag ist in Mitteleuropa von zahlreichen Bräuchen geprägt, darunter das Martinsgansessen, der Martinszug und das Martinssingen.\n\nQuelle: Wikipedia", Calendar.NOVEMBER, 11),
    VOLKSTRAUER  (0x00000400, "Volkstrauertag",             "Der Volkstrauertag ist in Deutschland ein staatlicher Gedenktag und gehört zu den sogenannten stillen Tagen. Er wird seit 1952 zwei Sonntage vor dem ersten Adventssonntag begangen und erinnert an die Kriegstoten und Opfer der Gewaltherrschaft aller Nationen.\n\nQuelle: Wikipedia"),
    TOTENS       (0x00000800, "Totensonntag",               "Der Totensonntag oder Ewigkeitssonntag ist in den evangelischen Kirchen in Deutschland und der Schweiz ein Gedenktag für die Verstorbenen. Er ist der letzte Sonntag vor dem ersten Adventssonntag und damit der letzte Sonntag des Kirchenjahres.\n\nSeit der Entwicklung des Kirchenjahres im Mittelalter wurden mit den letzten Sonntagen des Kirchenjahres liturgische Lesungen zu den Letzten Dingen verbunden. Während am drittletzten Sonntag das Thema „Tod“ im Mittelpunkt steht, hat der vorletzte Sonntag die Thematik „(Jüngstes) Gericht“ und der letzte „Ewiges Leben“.\n\nQuelle: Wikipedia"),
    ADV1         (0x00001000, "1. Advent",                  "Advent (lateinisch adventus „Ankunft“), eigentlich Adventus Domini (lat. für Ankunft des Herrn), bezeichnet die Jahreszeit, in der die Christenheit sich auf das Fest der Geburt Jesu Christi, Weihnachten, vorbereitet. Die Christen gedenken der Geburt Jesu und feiern sie als Menschwerdung Gottes. Zugleich erinnert der Advent daran, dass Christen das zweite Kommen Jesu Christi erwarten sollen. Mit dem ersten Adventssonntag beginnt für die römisch-katholische Kirche und die evangelische Kirche auch das neue Kirchenjahr.\n\nQuelle: Wikipedia"),
    NIKO         (0x00002000, "Nikolaus",                   "Nikolaus von Myra (geb. zw. 270 und 286 in Patara; gest. 6. Dezember zw. 326 und 365) ist einer der bekanntesten Heiligen der Ostkirchen und der lateinischen Kirche. Sein Gedenktag, der 6. Dezember, wird im gesamten Christentum mit zahlreichen Volksbräuchen begangen.\nNikolaus wirkte in der ersten Hälfte des 4. Jahrhunderts als Bischof von Myra in der kleinasiatischen Region Lykien, damals Teil des römischen, später des byzantinischen Reichs, heute der Türkei.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 6),
    ADV2         (0x00004000, "2. Advent",                  "Advent (lateinisch adventus „Ankunft“), eigentlich Adventus Domini (lat. für Ankunft des Herrn), bezeichnet die Jahreszeit, in der die Christenheit sich auf das Fest der Geburt Jesu Christi, Weihnachten, vorbereitet. Die Christen gedenken der Geburt Jesu und feiern sie als Menschwerdung Gottes. Zugleich erinnert der Advent daran, dass Christen das zweite Kommen Jesu Christi erwarten sollen. Mit dem ersten Adventssonntag beginnt für die römisch-katholische Kirche und die evangelische Kirche auch das neue Kirchenjahr.\n\nQuelle: Wikipedia"),
    ADV3         (0x00008000, "3. Advent",                  "Advent (lateinisch adventus „Ankunft“), eigentlich Adventus Domini (lat. für Ankunft des Herrn), bezeichnet die Jahreszeit, in der die Christenheit sich auf das Fest der Geburt Jesu Christi, Weihnachten, vorbereitet. Die Christen gedenken der Geburt Jesu und feiern sie als Menschwerdung Gottes. Zugleich erinnert der Advent daran, dass Christen das zweite Kommen Jesu Christi erwarten sollen. Mit dem ersten Adventssonntag beginnt für die römisch-katholische Kirche und die evangelische Kirche auch das neue Kirchenjahr.\n\nQuelle: Wikipedia"),
    ADV4         (0x00010000, "4. Advent",                  "Advent (lateinisch adventus „Ankunft“), eigentlich Adventus Domini (lat. für Ankunft des Herrn), bezeichnet die Jahreszeit, in der die Christenheit sich auf das Fest der Geburt Jesu Christi, Weihnachten, vorbereitet. Die Christen gedenken der Geburt Jesu und feiern sie als Menschwerdung Gottes. Zugleich erinnert der Advent daran, dass Christen das zweite Kommen Jesu Christi erwarten sollen. Mit dem ersten Adventssonntag beginnt für die römisch-katholische Kirche und die evangelische Kirche auch das neue Kirchenjahr.\n\nQuelle: Wikipedia"),
    HEILIGA      (0x00020000, "Heiligabend",                "Der Heilige Abend am 24. Dezember, auch Heiligabend oder Weihnachtsabend genannt, ist der Vorabend des Weihnachtsfestes; vielerorts wird auch der ganze Vortag so bezeichnet. Am Abend findet unter anderem in Deutschland, der Schweiz, in Liechtenstein und in Österreich traditionell die Bescherung statt. Als Heilige Nacht oder als Christnacht wird die Nacht vom 24. auf den 25. Dezember bezeichnet.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 24),
    SILVESTER    (0x00040000, "Silvester",                  "Als Silvester wird in einigen europäischen Sprachen der 31. Dezember, der letzte Tag des Jahres im westlichen Kulturraum, bezeichnet. Nach dem römisch-katholischen Heiligenkalender ist Papst Silvester I. (gest. 31. Dezember 335) der Tagesheilige. Auf Silvester folgt mit dem Neujahrstag der 1. Januar des folgenden Jahres.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 31),
    WALPURGIS    (0x00080000, "Walpurgisnacht",             "Die Walpurgisnacht (auch Hexenbrennen) ist ein traditionelles nord- und mitteleuropäisches Fest am 30. April. Der Name leitet sich von der heiligen Walburga ab, deren Gedenktag bis ins Mittelalter am Tag ihrer Heiligsprechung am 1. Mai gefeiert wurde. Die Walpurgisnacht war die Vigilfeier des Fests. Als „Tanz in den Mai“ hat sie wegen der Gelegenheit zu Tanz und Geselligkeit am Vorabend des Maifeiertags auch als urbanes, modernes Festereignis Eingang in private und kommerzielle Veranstaltungen gefunden.\n\nQuelle: Wikipedia", Calendar.APRIL, 30),
    
    /** World days / memorial days, part 1. */
    AFRIKA       (0x00000001, "Afrikatag",                  "Als Afrikatag wird in der katholischen Kirche seit über hundert Jahren der 6. Januar begangen.\nAn diesem Tag werden weltweit Spenden für Afrika, speziell für die Ausbildung und Förderung von Priestern, Ordensleuten und Katecheten, gesammelt.\n\nDiese Afrika-Kollekte wurde erstmals am 6. Januar 1891 auf Wunsch von Papst Leo XIII. durchgeführt, der damit Gelder zur Unterstützung des Kampfes gegen die Sklaverei in Afrika sammeln wollte. Es ist der älteste Tag einer gesamtkirchlichen Missionskollekte der katholischen Kirche. Der Tag der Erscheinung des Herrn wurde gewählt, weil Afrika in der Gestalt des schwarzen Königs seit ältester Zeit in der Tradition sichtbar vertreten ist.\n\nQuelle: Wikipedia", Calendar.JANUARY, 6),
    ARTENSCHUTZ  (0x00000002, "Tag des Artenschutzes",      "Der Tag des Artenschutzes (UN World Wildlife Day) ist ein im Rahmen des Washingtoner Artenschutzübereinkommens (CITES, Convention on International Trade in Endangered Species of Wild Fauna and Flora) eingeführter Aktions- und Gedenktag. Er findet jährlich am 3. März statt: Durch das am 3. März 1973 unterzeichnete Abkommen sollen bedrohte wildlebende Arten (Tiere und Pflanzen) geschützt werden, die durch Handelsinteressen gefährdet sind.\n\nQuelle: Wikipedia", Calendar.MARCH, 3),
    AUTOFREI     (0x00000004, "Autofreier Tag",             "Der Autofreie Tag ist ein Aktionstag, der in Europa von verschiedenen Organisationen (zum Beispiel Umweltverbänden und Kirchen) initiiert und unterstützt wird. Er findet jährlich am 22. September statt. In Kommunen, die an der Europäischen Woche der Mobilität teilnehmen, wird von diesem Datum jedoch auch gelegentlich um wenige Tage abgewichen.\n\nDer Gedanke, einen Tag pro Jahr generell auf den Gebrauch des Autos zu verzichten, wird bereits von fast allen Staaten der Europäischen Union und darüber hinaus von den meisten Kommunen und Städten unterstützt. Tausende Gemeinden in Deutschland, hunderte in der Schweiz und in Österreich und ebenso in anderen Ländern haben entsprechende Aufrufe erlassen.\n\nQuelle: Wikipedia", Calendar.SEPTEMBER, 22),
    BLUTSPENDE   (0x00000008, "Weltblutspendetag",          "Der Weltblutspendetag (engl. World Blood Donor Day, in Deutschland auch Weltblutspendertag genannt) wird am 14. Juni, dem Geburtstag von Karl Landsteiner (1868–1943), dem Entdecker der Blutgruppen, begangen.\n\nVier internationale Organisationen, die sich weltweit für sicheres Blut auf der Basis freiwilliger und unentgeltlicher Blutspenden einsetzen, haben den Tag im Jahr 2004 ausgerufen: die Weltgesundheitsorganisation (WHO), die Internationale Organisation der Rotkreuz- und Rothalbmondgesellschaften (IFRK), die Internationale Gesellschaft für Transfusionsmedizin (ISBT) und die Internationale Föderation der Blutspendeorganisationen (FIODS).\n\nQuelle: Wikipedia", Calendar.JUNE, 14),
    BODEN        (0x00000010, "Weltbodentag",               "Der Weltbodentag (engl. World Soil Day) ist ein internationaler Aktionstag am 5. Dezember. Er wurde von der Internationalen Bodenkundlichen Union (IUSS) im Rahmen ihres 17. Weltkongresses im August 2002 in Bangkok ernannt. Mit ihm soll ein jährliches Zeichen für die Bedeutung der natürlichen Ressource Boden gesetzt und für den Bodenschutz geworben werden.\n\nDer Boden des Jahres wird jedes Jahr am Weltbodentag für das folgende Jahr ausgerufen.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 5),
    BUCH         (0x00000020, "Welttag des Buches",         "Der Welttag des Buches und des Urheberrechts (kurz Weltbuchtag, englisch World Book and Copyright Day) am 23. April ist seit 1995 ein von der UNESCO weltweit eingerichteter Feiertag für das Lesen, für Bücher, für die Kultur des geschriebenen Wortes und auch für die Rechte ihrer Autoren.\n\nQuelle: Wikipedia", Calendar.APRIL, 23),
    DARWIN       (0x00000040, "Darwin-Tag",                 "Der Darwin-Tag ist ein weltweit gefeierter Gedenktag und wird jährlich am 12. Februar, dem Geburtstag Charles Darwins, begangen.\n\nDer Darwin-Tag versteht sich als Hommage an Darwins Beitrag zur Wissenschaft. Er soll der Öffentlichkeit auch generell die Naturwissenschaften näherbringen (promote public education about science) und die Naturwissenschaften und die Menschheit feiern.\n\nQuelle: Wikipedia", Calendar.FEBRUARY, 12),
    ANTI_DIET    (0x00000080, "Internationaler Anti-Diät-Tag", "Der Internationale Anti-Diät-Tag (englisch International No Diet Day – INDD abgekürzt) ist ein inoffizieller internationaler Aktionstag. Er wurde von der britischen Buchautorin und Feministin Mary Evans Young ins Leben gerufen und findet jährlich am 6. Mai statt.\n\nDie Ursprünge dieses Aktionstages liegen im Jahr 1992. Die von einer Anorexie geheilte Mary Evans Young gründete die Anti-Diäten-Kampagne Diet Breakers. Sie erhielt mediale Aufmerksamkeit, nachdem sie sich in Talkshows und TV-Interviews für die Akzeptanz des eigenen Körpers und gegen den Schlankheitswahn einsetzte. Als Antrieb gab Young sowohl ihr eigenes Schicksal als auch Medienberichte über Magenverkleinerungen und mögliche Komplikationen sowie Berichte über jugendliche Mädchen an, die Selbstmord begangen hätten, „weil sie es nicht mehr ertragen konnten, fett zu sein.“\n\nQuelle: Wikipedia", Calendar.MAY, 6),
    DROGEN       (0x00000100, "Weltdrogentag",              "Der „Weltdrogentag“, offiziell International Day against Drug Abuse and Illicit Trafficking oder Internationaler Tag gegen Drogenmissbrauch und unerlaubten Suchtstoffverkehr findet jährlich am 26. Juni statt. Dieser Aktionstag wurde im Dezember 1987 von der Generalversammlung der Vereinten Nationen festgelegt und ist gegen den Missbrauch von Drogen gerichtet. Ähnlich wie der Weltnichtrauchertag ist der Weltdrogentag jedes Jahr Anlass für Aktionen und Pressemitteilungen. Seitens der Vereinten Nationen ist das United Nations Office on Drugs and Crime (UNODC) für den „Weltdrogentag“ verantwortlich.\n\nQuelle: Wikipedia", Calendar.JUNE, 26),
    EHRENAMT     (0x00000200, "Internationaler Tag des Ehrenamtes", "Der Internationale Tag des Ehrenamtes (englisch International Volunteer Day for Economic and Social Development, IVD) ist ein jährlich am 5. Dezember abgehaltener Gedenk- und Aktionstag zur Anerkennung und Förderung ehrenamtlichen Engagements. Er wurde 1985 von der UN mit Wirkung ab 1986 beschlossen. In Deutschland ersetzt er de facto den Tag des Ehrenamts, der früher am 2. Dezember begangen wurde. An diesem Tag wird auch der Verdienstorden der Bundesrepublik Deutschland an besonders engagierte Personen vergeben.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 5),
    ERDE         (0x00000400, "Tag der Erde",               "Der Tag der Erde (englisch Earth Day) wird alljährlich am 22. April in über 175 Ländern begangen und soll die Wertschätzung für die natürliche Umwelt stärken, aber auch dazu anregen, die Art des Konsumverhaltens zu überdenken.\n\nQuelle: Wikipedia", Calendar.APRIL, 22),
    ERFINDER     (0x00000800, "Tag der Erfinder",           "Der Tag der Erfinder wird in verschiedenen Ländern an unterschiedlichen Tagen gefeiert. In Deutschland, Österreich und der Schweiz ist am 9. November, dem Geburtstag der Erfinderin und Hollywoodschauspielerin Hedy Lamarr, der Tag der Erfinder.\nLaut Internetpräsentation verfolgen die Veranstalter mit dem Tag der Erfinder folgende Ziele:\n - Mut zu eigenen Ideen und zur Veränderung\n - Erinnern an vergessene Erfinder\n - Erinnern an große Erfinder, die unser Leben verbessert haben\n - Den Ruf zeitgenössischer Erfinder und Visionäre verbessern\n - Zur Mitarbeit an unserer Zukunft aufrufen\n\nQuelle: Wikipedia", Calendar.NOVEMBER, 9),
    ERNAEHRUNG   (0x00001000, "Welternährungstag",          "Der Welternährungstag (auch Welthungertag) findet jedes Jahr am 16. Oktober statt und soll darauf aufmerksam machen, dass weltweit über eine Milliarde Menschen an Hunger leiden.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 16),
    FAHRRAD      (0x00002000, "Eurpoäischer Tag des Fahrrads", "Der Europäische Tag des Fahrrades ist ein Aktionstag in einigen europäischen Ländern und findet seit 1998 jährlich am 3. Juni statt.\nEr wurde anlässlich der zunehmenden problembehafteten Verkehrsdichte durch motorisierte Fortbewegungsmittel eingeführt und soll darauf hinweisen, dass das Fahrrad das umweltfreundlichste, gesündeste und sozial verträglichste Fortbewegungsmittel darstellt. So finden an diesem Datum beziehungsweise Anfang Juni mit Blick auf diesen Tag verschiedene Aktionen und Sternfahrten statt. Das Statistische Bundesamt stellt anlässlich des Tages aktuelle Statistiken zur Fahrradnutzung in Deutschland aus.\n\nQuelle: Wikipedia", Calendar.JUNE, 3),
    F_D_MUSIQUE  (0x00004000, "Fête de la Musique",         "Die Fête de la Musique (französisch für „[das] Fest der Musik“) ist eine Veranstaltung bei der Amateur- und Berufsmusiker, Performer im Bereich Musik, DJs usw. im öffentlichen Raum honorarfrei auftreten (zum Beispiel auf öffentlichen bzw. öffentlich zugänglichen Plätzen, Fußwegen/Wegen, in Parks/Gärten/Vorgärten/Höfen, vor bzw. in Cafés/Bars/Kneipen/Restaurants, Museen/Galerien, Kirchen, Kiosken/Geschäften usw.). Es wird kein Eintrittsgeld verlangt.\n\nDie Fête de la Musique findet jedes Jahr am 21. Juni, dem kalendarischen Sommeranfang, statt – in mehr als 540 Städten weltweit, davon über 300 Städte in Europa. Deutschlandweit beteiligen sich mittlerweile mehr als 50 Städte an dem Fest.\n\nQuelle: Wikipedia", Calendar.JUNE, 21),
    GEBET        (0x00008000, "Weltgebetstag",              "Der Weltgebetstag (WGT, auch bekannt unter: Weltgebetstag der Frauen) ist die größte ökumenische Basisbewegung von Frauen. Ihr Motto lautet: „Informiert beten – betend handeln“. Der Weltgebetstag wird in über 170 Ländern in ökumenischen Gottesdiensten begangen. Vor Ort bereiten Frauen unterschiedlicher Konfessionen gemeinsam die Gestaltung und Durchführung der Gottesdienste vor. Jedes Jahr schreiben Frauen aus einem anderen Land der Welt die Gottesdienstordnung zum Weltgebetstag. Der Weltgebetstag findet jeweils am ersten Freitag im März statt.\n\nQuelle: Wikipedia"),
    GESUNDHEIT   (0x00010000, "Weltgesundheitstag",         "Der Weltgesundheitstag ist ein weltweiter Aktionstag, mit dem die WHO an ihre Gründung im Jahre 1948 erinnern möchte. Er wird seit dem Jahr 1954 jährlich am 7. April begangen. Jedes Jahr soll am Aktionstag ein vorrangiges Gesundheitsproblem in das Bewusstsein der Weltöffentlichkeit gerückt werden. Dabei geht es zunehmend um Themen, die bei der Entwicklung von nationalen Gesundheitssystemen helfen sollen. In Deutschland wird der frühere Festtag immer mehr zu einer Informationsplattform für Gesundheitsberufe mit Fachtagungen und Kongressen.\n\nQuelle: Wikipedia", Calendar.APRIL, 7),
    BIER         (0x00020000, "Internationaler Tag des Bieres", "Der Internationale Tag des Bieres (engl. International Beer Day) findet jährlich am ersten Freitag im August statt. An diesem Tag wird weltweit das Getränk Bier gefeiert und getrunken.\nDer Internationale Tag des Bieres verfolgt drei Ziele:\n - Freunde treffen, um gemeinsam Bier zu genießen.\n - Die Männer und Frauen zu ehren, welche das Bier brauen und servieren.\n - Gemeinsam die Biere aller Nationen und Kulturen zu feiern und damit die Welt zu vereinen.\n\nQuelle: Wikipedia"),
    JAZZ         (0x00040000, "Internationaler Tag des Jazz", "Internationaler Tag des Jazz ist der 30. April. Die 36. Generalkonferenz der UNESCO hatte ihn im November 2011 ausgerufen. Ziel des Gedenk- und Aktionstages ist, an „die künstlerische Bedeutung des Jazz, seine Wurzeln und seine weltweiten Auswirkungen auf die kulturelle Entwicklung erinnern.“\nEr wurde 2012 erstmals begangen; an der Auftaktveranstaltung in Paris am 27. April wirkte auch Klaus Doldinger mit.\n\nQuelle: Wikipedia", Calendar.APRIL, 30),
    KAUF_NIX     (0x00080000, "Kauf-Nix-Tag",               "Der Kauf-Nix-Tag (englisch: „Buy Nothing Day“) ist ein konsumkritischer Aktionstag am letzten Freitag (Nordamerika) bzw. Samstag (Europa) im November. Dieser wird mittlerweile in etwa 45 Ländern organisiert.\nDurch einen 24-stündigen Konsumverzicht soll mit dem Buy Nothing Day gegen „ausbeuterische Produktions- und Handelsstrategien internationaler Konzerne und Finanzgruppen“ protestiert werden. Außerdem soll zum Nachdenken über das eigene Konsumverhalten und die weltweiten Auswirkungen angeregt werden. Ein bewusstes, auf Nachhaltigkeit abzielendes Kaufverhalten jedes Einzelnen soll somit gefördert werden.\n\nQuelle: Wikipedia"),
    LACH         (0x00100000, "Weltlachtag",                "Der Weltlachtag ist ein Welttag, der jährlich am ersten Sonntag im Mai begangen wird. Die Idee stammt aus der Yoga-Lachbewegung, die weltweit in über 6.000 Lachclubs in mehr als 100 Ländern auf allen Kontinenten organisiert ist. Punkt 14:00 Uhr deutscher Zeit (12:00 GMT) wird dabei in Europa gemeinsam für eine Minute gelacht.\nDer Weltlachtag wurde 1998 von Madan Kataria, dem Gründer der weltweiten Lachyoga-Bewegung, ins Leben gerufen. Die Feier des Weltlachtags soll den Weltfrieden verkörpern und hat das Ziel, ein globales Bewusstsein der Gesundheit, des Glücks und des Friedens durch das Lachen zu erreichen.\n\nQuelle: Wikipedia"),
    LEHRER       (0x00200000, "Weltlehrertag",              "Der Weltlehrertag wird seit 1994 jährlich am 5. Oktober begangen, im Gedenken an die „Charta zum Status der Lehrerinnen und Lehrer“, die 1964 von der UNESCO und der Internationalen Arbeitsorganisation angenommen wurde.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 5),
    MAENNER      (0x00400000, "Weltmännertag",              "Der Weltmännertag ist ein Aktionstag zur Männergesundheit, der seit dem Jahr 2000 jährlich am 3. November stattfindet. Dieser sollte laut Aussage des Schirmherrn Michail Gorbatschow das Bewusstsein der Männer im gesundheitlichen Bereich erweitern. So liege die Lebenserwartung der Männer im Durchschnitt sieben Jahre unter der der Frauen. Neben Männergesundheit waren in Deutschland auch Wehrpflicht und Zukunftsperspektiven für Jungen Themenschwerpunkte.\n\nQuelle: Wikipedia", Calendar.NOVEMBER, 3),
    MEER         (0x00800000, "Tag des Meeres",             "Der Tag des Meeres hat seinen Ursprung im Erdgipfel am 8. Juni 1992 in Rio de Janeiro. Seit 2009 wird der 8. Juni als Tag des Meeres von den Vereinten Nationen begangen. Die Ozeane werden als bedeutend für Ernährungssicherheit, Gesundheit und dem Überleben allen Lebens, für das Klima und als ein kritischer Teil der Biosphäre gesehen. Ziel des Tages ist es daher, weltweit Aufmerksamkeit für aktuelle Herausforderungen im Zusammenhang mit den Ozeanen zu erlangen.\n\nQuelle: Wikipedia", Calendar.JUNE, 8),
    METEOROLOG   (0x01000000, "Welttag der Meteorologie",   "Der Welttag der Meteorologie, Internationaler Tag der Meteorologie bzw. Weltwettertag findet jährlich weltweit am 23. März statt und soll an die 1950 in Kraft getretene Konvention der Weltorganisation für Meteorologie (WMO), die ihren Sitz in Genf hat, erinnern. Damals begann eine friedliche Zusammenarbeit zwischen den verschiedensten Nationen, die ohne Beispiel war. Deutschland wird in dieser Organisation seit 1954 durch den Deutschen Wetterdienst (DWD) vertreten, Österreich trat 1957 bei. Man hatte erkannt, dass sich aus weltumspannenden aktuellen Wettermeldungen verlässlichere Wetterprognosen erstellen ließen. So konnten selbst Kriege oder andere Ideologien den Datenaustausch nicht verhindern. Selbst Krisengebiete kooperierten ohne Rücksicht auf „politische Großwetterlagen“\n\nQuelle: Wikipedia", Calendar.MARCH, 23),
    MUSEUM       (0x02000000, "Internationaler Museumstag", "Der Internationale Museumstag ist ein seit 1978 jährlich stattfindendes internationales Ereignis, bei dem am dritten Sonntag im Mai auf die Vielfalt und Bedeutung der Museen aufmerksam gemacht wird. In Deutschland steht der Tag unter der Schirmherrschaft des amtierenden Bundesratspräsidenten.\nZahlreiche Museen – von den Heimat- und Regionalmuseen bis hin zu den großen staatlichen Einrichtungen – präsentieren sich an diesem Tag mit besonderen Aktionen wie Sonderführungen, einem Blick hinter die Kulissen, Workshops, Museumsfesten und langen Museumsnächten bei freiem Eintritt.\n\nQuelle: Wikipedia"),
    MUSIK        (0x04000000, "Weltmusiktag",               "Der Weltmusiktag ist ein Tag, der international der Musik gewidmet ist. Er findet jährlich am 1. Oktober statt.\nDer Weltmusiktag wurde 1975 vom Internationalen Musikrat unter der Leitung des damaligen IMC-Präsidenten Yehudi Menuhin (USA) ins Leben gerufen, um Musik in allen Bevölkerungsgruppen zu fördern und entsprechend den Idealen der UNESCO (Friede und Freundschaft der Völker) eine gegenseitige Anerkennung der künstlerischen Werte sicherzustellen sowie den internationalen Erfahrungsaustausch im Bereich der Musik zu fördern. Anlässlich des Weltmusiktages finden vielfältige Aktionen -insbesondere Konzerte- statt.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 1),
    MAEDCHEN     (0x08000000, "Internationaler Mädchentag", "Der Internationale Mädchentag (auch Welt-Mädchentag genannt, englisch: International Day of the Girl Child) ist ein von den Vereinten Nationen (UNO) initiierter Aktionstag. Er soll in jedem Jahr am 11. Oktober einen Anlass geben, um auf die weltweit vorhandenen Benachteiligungen von Mädchen hinzuweisen.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 11),
    NICHTRAUCH   (0x10000000, "Weltnichtrauchertag",        "Der Weltnichtrauchertag findet alljährlich am 31. Mai statt; die Weltgesundheitsorganisation (WHO) hat ihn 1987 ins Leben gerufen.\nÄhnlich wie der Weltdrogentag gibt der Weltnichtrauchertag jedes Jahr Anlass für Aktionen und Pressemitteilungen der Gesundheitsorganisationen und -initiativen.\n\nQuelle: Wikipedia", Calendar.MAY, 31),
    PHILOSOPHIE  (0x20000000, "Welttag der Philosophie",    "Der Welttag der Philosophie wird weltweit am dritten Donnerstag im November begangen.\nDie UNESCO-Generalkonferenz 2005 erklärte in der Resolution 33C/Res. 37, dass der weltweit begangene Aktionstag „der Philosophie zu größerer Anerkennung verhelfen und ihr und der philosophischen Lehre Auftrieb verleihen“ soll. Alle Mitgliedsländer werden in dieser Resolution aufgefordert, den Tag in Schulen, Hochschulen, Institutionen, Städten oder philosophischen Vereinen aktiv zu gestalten.\n\nQuelle: Wikipedia"),
    RADIO        (0x40000000, "Welttag des Radios",         "Der Welttag des Radios (englisch World Radio Day; kurz: Weltradiotag) wird am 13. Februar begangen. Er fand 2012 zum ersten Mal statt. Die Generalkonferenz der UNESCO hat den Weltradiotag in Erinnerung an die Gründung des United Nations Radio am 13. Februar 1946 aufgerufen.\n\nQuelle: Wikipedia", Calendar.FEBRUARY, 13),
    SPIEL        (0x80000000, "Weltspieltag",               "Der Weltspieltag ist ein internationaler Aktionstag. Er wird jedes Jahr am 28. Mai begangen.\nWunsch der Veranstalter ist es, Kinder und Erwachsene verschiedener sozialer Schichten durch das Spielen einander näher zu bringen und den Spaß am Spielen zu fördern.\n\nQuelle: Wikipedia", Calendar.MAY, 28),
    
    /** World days / memorial days, part 2. */
    AIDS         (0x00000001, "Welt-AIDS-Tag",              "Der Welt-AIDS-Tag wird jährlich von der UNAIDS – der AIDS-Organisation der Vereinten Nationen – organisiert und findet am 1. Dezember statt.\nRund um den Globus erinnern am 1. Dezember verschiedenste Organisationen an das Thema AIDS und rufen dazu auf, aktiv zu werden und Solidarität mit HIV-Infizierten, AIDS-Kranken und den ihnen nahestehenden Menschen zu zeigen. Der Welt-AIDS-Tag dient auch dazu, Verantwortliche in Politik, Massenmedien, Wirtschaft und Gesellschaft – weltweit wie auch in Europa und Deutschland – daran zu erinnern, dass die HIV-/AIDS-Pandemie weiter besteht.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 1),
    ALZHEIMER    (0x00000002, "Welt-Alzheimertag",          "Welt-Alzheimertag ist der 21. September. Seit 1994 finden am 21. September in aller Welt vielfältige Aktivitäten statt, um die Öffentlichkeit auf die Situation der Alzheimer-Kranken und ihrer Angehörigen aufmerksam zu machen. Weltweit sind etwa 35 Millionen Menschen von Demenzerkrankungen betroffen, zwei Drittel davon in Entwicklungsländern. Bis 2050 wird die Zahl auf voraussichtlich 115 Millionen ansteigen, besonders dramatisch in China, Indien und Lateinamerika.\n\nQuelle: Wikipedia", Calendar.SEPTEMBER, 21),
    HAND_WASCH   (0x00000004, "Internationaler Hände-Waschtag", "Der Internationale Hände-Waschtag wurde von der Weltgesundheitsorganisation (WHO) ins Leben gerufen und findet jährlich am 15. Oktober statt; zum ersten Mal wurde er im Jahr 2008 begangen.\nEin regelmäßiges und ordentliches Waschen der Hände – nach Einseifen Reiben nicht unter einer halben Minute und besonders auch der Fingerkuppen und der Daumen – verhindert die Ausbreitung von Infektionskrankheiten wie beispielsweise des Influenza-A-Virus H1N1, da ein Großteil aller ansteckenden Krankheiten über die Hände übertragen wird.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 15),
    HEPATITS     (0x00000008, "Welt-Hepatitis-Tag",         "Schätzungsweise 500 Millionen Menschen weltweit haben entweder Hepatitis B oder Hepatitis C. Dies bedeutet, dass statistisch gesehen weltweit jeder 12. Mensch von Hepatitis betroffen ist.\nDer Welt-Hepatitis-Tag verfolgt das Ziel der globalen Sensibilisierung der Bevölkerung zu den Themen Hepatitis B und Hepatitis C und zur Ermutigung von Prävention, Diagnose und Behandlung. Seit 2011 wird er am 28. Juli, dem Geburtstag von Baruch Samuel Blumberg, begangen.\n\nQuelle: Wikipedia", Calendar.JULY, 28),
    KINDERBUCH   (0x00000010, "Internationaler Kinderbuchtag", "Der Internationale Kinderbuchtag (englisch International Children’s Book Day) ist ein internationaler Aktionstag, der die Freude am Lesen unterstützen und das Interesse an Kinder- und Jugendliteratur fördern soll. Er wird seit dem Jahr 1967 jährlich am 2. April, dem Geburtstag des bekannten Dichters und Schriftstellers Hans Christian Andersen, begangen und wurde durch das International Board on Books for Young People (IBBY) gegründet.\n\nQuelle: Wikipedia", Calendar.APRIL, 2),
    FERNMELDE    (0x00000020, "Weltfernmeldetag",           "Der Weltfernmeldetag (en.: World Information Society Day) wurde 1967 durch die Internationale Fernmeldeunion ausgerufen und ist von der UNO als internationaler Gedenktag anerkannt. Der Tag findet, in Anlehnung an das Gründungsdatum der Internationalen Fernmeldeunion ITU im Jahre 1865, jährlich am 17. Mai statt.\n\nQuelle: Wikipedia", Calendar.MAY, 17),
    ANTI_KORRUP  (0x00000040, "Welt-Anti-Korruptionstag",   "Der Welt-Anti-Korruptions-Tag wird seit 2003 am 9. Dezember begangen, als das Übereinkommen der Vereinten Nationen gegen Korruption in Mérida (Mexiko) zur Unterzeichnung vorlag.\nEr ist eine jährliche Veranstaltung der UN, also gefördert von den Vereinten Nationen mit dem Ziel, das Bewusstsein für Korruption und damit zusammenhängende Fragen zu verstärken, und die Menschen, die Korruption in ihren Gemeinden und Regierungen bekämpfen, aufzuzeigen.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 9),
    KRANKE       (0x00000080, "Welttag der Kranken",        "Der Welttag der Kranken wurde 1993 anlässlich des Gedenkens an alle von Krankheiten heimgesuchten und gezeichneten Menschen von Papst Johannes Paul II. eingeführt. Er wird jährlich am 11. Februar, dem Gedenktag Unserer Lieben Frau in Lourdes begangen. Neben einem Gottesdienst im Petersdom finden jeweils zentrale Veranstaltungen in einem anderen Land statt.\n\nQuelle: Wikipedia", Calendar.FEBRUARY, 11),
    KULTUR_ENTW  (0x00000100, "Welttag für kulturelle Entwicklung", "Der Welttag der kulturellen Vielfalt für Dialog und Entwicklung (kurz auch Welttag für kulturelle Entwicklung; englisch World Day for Cultural Diversity, for Dialogue and Development) ist ein Aktionstag der UNESCO, der jährlich am 21. Mai begangen wird.\nAnlässlich der von der 31. Generalversammlung der UNESCO im November 2001 verabschiedeten Allgemeinen Erklärung zur kulturellen Vielfalt wurde auch der Welttag für kulturelle Entwicklung ausgerufen. Er soll Bewusstsein für kulturelle Vielfalt schaffen und den Beitrag von Künstlern zum Dialog der Kulturen betonen.\n\nQuelle: Wikipedia", Calendar.MAY, 21),
    LEPRA        (0x00000200, "Welt-Lepra-Tag",             "Der Welt-Lepra-Tag ist ein internationaler Gedenk- und Aktionstag, welcher jährlich am letzten Sonntag im Januar begangen wird.\nDer Welt-Lepra-Tag wurde 1954 von dem Franzosen Raoul Follereau, „Apostel der Leprakranken“ eingeführt. Er wollte damit auf die Not der Betroffenen aufmerksam machen. Damals zählte die WHO ca. 15 Millionen Leprakranke weltweit, eine Heilung war nicht möglich.\nHeute ist Lepra im frühen Stadium heilbar, aber noch immer nicht ausgerottet. Jährlich erkranken über 200.000 Menschen daran. Die Bedeutung des Welt-Lepra-Tages ist daher immer noch aktuell und inzwischen eine feste Institution in etwa 130 Ländern der Welt.\n\nQuelle: Wikipedia"),
    NIEREN       (0x00000400, "Weltnierentag",              "Der Weltnierentag wurde 2006 ins Leben gerufen und steht seither jedes Jahr unter einem besonderen Motto, an dem sich die Aktivitäten der nationalen Organisationen orientieren. Er findet jährlich am 2. Donnerstag im März statt.\nDie Ziele des internationalen Weltnierentages sind es die Bedeutung des Organs im menschlichen Organismus aufzuzeigen und das Bewusstsein über die enorme Leistung unserer Nieren zu steigern. Die Häufigkeit und die Auswirkungen von chronischen Nierenerkrankungen und die damit verbundenen gesundheitlichen Probleme sollen weltweit reduziert werden.\n\nQuelle: Wikipedia"),
    POLIO        (0x00000800, "Welt-Poliotag",              "Der Welt-Poliotag (englisch World Polio Day) wird jährlich am 28. Oktober begangen.\nDer Aktions- und Gedenktag wurde von der Weltgesundheitsorganisation (WHO) erstmals im Jahr 1988 ins Leben gerufen. Hintergrund für den Tag ist die weltweite Kampagne der WHO zur Ausrottung von Polio Global polio eradication initiative (GPEI).\nDas Datum wurde gewählt, weil der Entwickler des ersten Impfstoffs gegen Polio (Kinderlähmung), Jonas Salk, an diesem Tag geboren worden ist.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 28),
    WFMA         (0x00001000, "Welttag für menschenwürdige Arbeit", "Der Welttag für menschenwürdige Arbeit (WFMA, englisch world day for decent work, WDDW) wird jährlich am 7. Oktober begangen.\nDer Aktionstag wurde vom Internationalen Gewerkschaftsbund (IGB) bei dessen Neugründung im Jahr 2006 als internationaler Tag für Gute Arbeit ins Leben gerufen.\nAn diesem Tag treten die Gewerkschaften weltweit und öffentlich für die Herstellung menschenwürdiger Arbeitsbedingungen ein und weisen damit auf ein Hauptanliegen des IGB hin.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 7),
    SOZ_KOMM     (0x00002000, "Welttag der sozialen Kommunikationsmittel", "Der Tag der sozialen Kommunikationsmittel wurde 1967 von Papst Paul VI. als Welttag der Massenmedien eingeführt. Er findet sechs Wochen nach Ostersonntag bzw. drei Tage nach Christi Himmelfahrt statt. Im Blick auf diesen Tag veröffentlicht der jeweilige Papst zum Fest des heiligen Franz von Sales – des Patrons der Journalisten (24. Januar) – eine Botschaft, die die Christliche Soziallehre bezüglich der Ethik der Massenmedien erläutert.\n\nQuelle: Wikipedia"),
    SPORT        (0x00004000, "Internationaler Tag des Sports", "Der Internationale Tag des Sports für Entwicklung und Frieden (englisch international day of sport for development and peace) wird jährlich am 6. April begangen.\nDer Gedenktag wurde von der Generalversammlung der Vereinten Nationen am 23. August 2013 in der Resolution A/RES/67/296 einstimmig beschlossen. Er soll die inneren Werte des Sports, wie Fairness, Zusammenarbeit und Respekt für den Gegner, fördern.\n\nQuelle: Wikipedia", Calendar.APRIL, 6),
    SPRACHEN     (0x00008000, "Europäischer Tag der Sprachen", "Der Europäische Tag der Sprachen geht auf eine Initiative des Europarates zurück. Ziel des Aktionstages ist es, zur Wertschätzung aller Sprachen und Kulturen beizutragen, den Menschen die Vorteile von Sprachkenntnissen bewusst zu machen, die individuelle Mehrsprachigkeit zu fördern und die Menschen in Europa zum lebensbegleitenden Lernen von Sprachen zu motivieren. Dabei soll das reiche Erbe der 200 europäischen Sprachen bewahrt werden.\n\nQuelle: Wikipedia", Calendar.SEPTEMBER, 26),
    TANZ         (0x00010000, "Welttag des Tanzes",         "Der Welttanztag wurde vom Internationalen Komitee des Tanzes des Internationalen Theater Institutes (ITI der UNESCO) angeregt, und im Jahr 1982 erstmals ausgerufen, um den Tanz als universelle Sprache in der Welt zu würdigen. Er findet weltweit jedes Jahr am 29. April statt, dem Geburtstag des französischen Tänzers und Choreografen Jean-Georges Noverre (1727–1810), dem Gründer des modernen Ballets.\n\nQuelle: Wikipedia", Calendar.APRIL, 29),
    TIERSCHUTZ   (0x00020000, "Welttierschutztag",          "Der Welttierschutztag ist ein internationaler Aktionstag für den Tierschutz, der am 4. Oktober begangen wird.\nAn diesem Tag gedenkt man des Heiligen Franz von Assisi (Namenstag), der am Abend des 3. Oktober 1226 gestorben ist und der als Gründer des Franziskanerordens unter anderem wegen seiner Tierpredigten berühmt und volkstümlich wurde.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 4),
    TOILETTEN    (0x00040000, "Welttoilettentag",           "Als Welttoilettentag wurde der 19. November erstmals 2001 von der Welttoilettenorganisation ausgerufen. Am 24. Juli 2013 hat die Generalversammlung der Vereinten Nationen einstimmig, auf Vorschlag Singapurs, den 19. November zum Welt-Toiletten-Tag der Vereinten Nationen erklärt, im Kampf für Sanitäranlagen. Hintergrund ist das Fehlen ausreichend hygienischer Sanitäreinrichtungen für mehr als 40 Prozent der Weltbevölkerung und dadurch bedingt verschmutztes Wasser sowie wasserbürtige Krankheiten, was gesundheitliche und sozio-ökonomische Folgen nach sich zieht.\n\nQuelle: Wikipedia", Calendar.NOVEMBER, 19),
    TOURISMUS    (0x00080000, "Welttourismustag",           "Den Welttourismustag begeht die Welttourismusorganisation (UNWTO) seit 1980 jedes Jahr am 27. September. Das Datum geht zurück auf die Ratifizierung der UNWTO-Statuten im Jahr 1970.\nDer Welttourismustag zeigt die Bedeutung des Tourismus für die internationale Gemeinschaft sowie seine Auswirkungen auf soziale, kulturelle, politische und wirtschaftliche Werte weltweit.\n\nQuelle: Wikipedia", Calendar.SEPTEMBER, 27),
    TUBERKULOSE  (0x00100000, "Welt-Tuberkulosetag",        "Der Welttuberkulosetag (Abk.: Welt-TB-Tag) ist ein Gedenktag und fällt auf den 24. März eines jeden Jahres, um so die Erinnerung an die Tuberkulose in der Öffentlichkeit wachzuhalten, durch die auch heute noch jedes Jahr etwa 940.000 Menschen sterben (die meisten davon in der Dritten Welt).\nDieser Tag im Jahr wurde bewusst gewählt, denn am 24. März 1882 gab Robert Koch in Berlin die Entdeckung des Tuberkulose-Bakteriums bekannt. Zu dieser Zeit war die Tuberkulose in Europa und Amerika derart verbreitet, dass jeder siebte daran starb. Durch die Entdeckung der Krankheitsursache war es möglich, eine Therapie gegen die Krankheit zu entwickeln.\n\nQuelle: Wikipedia", Calendar.MARCH, 24),
    UMWELT       (0x00200000, "Weltumwelttag",              "Der Weltumwelttag oder auch Tag der Umwelt ist ein Aktionstag, der am 5. Juni gefeiert wird.\nAm 5. Juni 1972, dem Eröffnungstag der ersten Weltumweltkonferenz in Stockholm, wurde der Weltumwelttag offiziell vom United Nations Environment Programme (Umweltprogramm der Vereinten Nationen) ausgerufen. Seitdem beteiligen sich weltweit jährlich rund 150 Staaten an diesem World Environment Day (WED). Seit 1976 werden zum Weltumwelttag auch in Deutschland Aktionen zum Recycling, zur Naturzerstörung weltweit und zur Schärfung des Umweltbewusstseins organisiert.\n\nQuelle: Wikipedia", Calendar.JUNE, 5),
    VEGAN        (0x00400000, "Weltvegantag",               "Der Weltvegantag (englisch World Vegan Day) ist ein internationaler Aktionstag, der erstmals am 1. November 1994 anlässlich des fünfzigsten Jahrestags der Gründung der Vegan Society stattfand und seitdem jährlich am 1. November gefeiert wird.\nIn Deutschland finden am Weltvegantag mehrere Informationsveranstaltungen und Aktionen rund um das vegane Leben statt.\n\nQuelle: Wikipedia", Calendar.NOVEMBER, 1),
    VERBRAUCHER  (0x00800000, "Weltverbrauchertag",         "Der Weltverbrauchertag (englisch: World Consumer Rights Day (WCRD)) ist ein Aktionstag der internationalen Verbraucherorganisation Consumers International, an dem auf Verbraucherbelange öffentlich aufmerksam gemacht wird. Er wird seit dem Jahr 1983 jährlich am 15. März begangen. Der Weltverbrauchertag geht zurück auf den US-Präsidenten John F. Kennedy, der am 15. März 1962 vor dem amerikanischen Kongress drei grundlegende Verbraucherrechte proklamierte.\n\nQuelle: Wikipedia", Calendar.MARCH, 15),
    WASSER       (0x01000000, "Weltwassertag",              "Der Weltwassertag findet seit 1993 jedes Jahr am 22. März statt. Seit 2003 wird er von UN-Water organisiert.\nNeben den UN-Mitgliedsstaaten haben auch einige Nichtstaatliche Organisationen, die für sauberes Wasser und Gewässerschutz kämpfen, den Weltwassertag dazu genutzt, die öffentliche Aufmerksamkeit auf die kritischen Wasserthemen unserer Zeit zu lenken. Eine Milliarde Menschen haben keinen Zugang zu sicherem und sauberem Trinkwasser und vielfach spielt die Geschlechtszugehörigkeit eine Rolle beim Wasserzugang.\n\nQuelle: Wikipedia", Calendar.MARCH, 22),
    RHEUMA       (0x02000000, "Welt-Rheumatag",             "Der Welt-Rheuma-Tag (engl.: world arthritis day) wurde erstmals 1996 von der Arthritis and Rheumatism International (ARI) ins Leben gerufen, der internationalen Vereinigung von Selbsthilfeverbänden Rheumabetroffener. Ziel ist es, die Anliegen rheumakranker Menschen an diesem Tag in das Bewusstsein der Öffentlichkeit zu rücken. Der Welt-Rheuma-Tag findet immer am 12. Oktober weltweit statt.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 12),
    OSTEOP       (0x04000000, "Welt-Osteoporosetag",        "Der Weltosteoporosetag (englisch world osteoporosis day) wird jährlich am 20. Oktober begangen.\nSeit 1999 steht der Tag in jedem Jahr unter einem eigenen Motto, das auf einen Aspekt der Gefährdung, an Osteoporose zu erkranken oder eine Erkrankung zu erkennen oder zu vermeiden, hinweist. Zum Weltosteoporosetag veranstalten die Internationale Osteoporose-Stiftung und die nationalen medizinischen Fachgesellschaften sowie die Selbsthilfegruppen und ihre Verbände entsprechende Aufklärungsveranstaltungen über die Krankheit.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 20),
    WIND         (0x08000000, "Global Wind Day",            "Der Global Wind Day (im Deutschsprachigen Raum auch als Tag des Windes bezeichnet) ist ein Aktionstag der Windenergiebranche, der seit 2007 jährlich am 15. Juni begangen wird.\nZiel des Aktionstages ist Öffentlichkeitsarbeit. Mittels verschiedener Aktion wie Foto- und Malwettbewerben, Drachensteigen, Besichtigungen von Windparks und ggf. Besteigungen von Windkraftanlagen usw. soll auf die Bedeutung der Windenergienutzung aufmerksam gemacht werden.\n\nQuelle: Wikipedia", Calendar.JUNE, 15),
    WISSENSCH    (0x10000000, "Welttag der Wissenschaft",   "Der Welttag der Wissenschaft für Frieden und Entwicklung (englisch: World Science Day for Peace and Development, WSDPD) ist ein von der Organisation der Vereinten Nationen für Erziehung, Wissenschaft und Kultur (UNESCO) ausgerufener weltweiter Gedenk- und Aktionstag, der an den bedeutenden Beitrag der Wissenschaften für Frieden und Entwicklung erinnern soll. Außerdem soll er die Beiträge der UNESCO zur Wissenschaft würdigen. Im Rahmen des Welttages der Wissenschaft findet die Verleihung der UNESCO-Wissenschaftspreise statt.\n\nQuelle: Wikipedia", Calendar.NOVEMBER, 10),
    FEUCHT       (0x20000000, "Welttag der Feuchgebiete",   "Der Welttag der Feuchtgebiete wird seit 1997 jährlich am 2. Februar begangen, im Gedenken an die Ramsar-Vereinbarung (Übereinkommen über Feuchtgebiete, insbesondere als Lebensraum für Wasser- und Watvögel, von internationaler Bedeutung), die von der UNESCO angestoßen wurde. Der Tag soll die öffentliche Wahrnehmung des Wertes und der Vorzüge von Feuchtgebieten verbessern.\n\nQuelle: Wikipedia", Calendar.FEBRUARY, 2),
    MENSCHEN_RE  (0x40000000, "Tag der Menschenrechte",     "Der Tag der Menschenrechte wird am 10. Dezember gefeiert und ist der Gedenktag zur Allgemeinen Erklärung der Menschenrechte, die am 10. Dezember 1948 durch die Generalversammlung der Vereinten Nationen verabschiedet wurde.\nMenschenrechtsorganisationen wie Amnesty International nehmen diesen Tag jedes Jahr zum Anlass, die Menschenrechtssituation weltweit kritisch zu betrachten und auf aktuelle Brennpunkte hinzuweisen.\nDas Europäische Parlament verleiht um diesen Tag jährlich den Sacharow-Preis, die Organisation Reporter ohne Grenzen ihren Menschenrechtspreis.\nJedes Jahr am 10. Dezember, dem Todestag Alfred Nobels, wird in Oslo (Norwegen) der Friedensnobelpreis verliehen.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 10),
    KREBS        (0x80000000, "Weltkrebstag",               "Der Weltkrebstag findet jährlich am 4. Februar statt und hat zum Ziel, die Vorbeugung, Erforschung und Behandlung von Krebserkrankungen ins öffentliche Bewusstsein zu rücken. Er wurde 2006 von der Union internationale contre le cancer, der Weltgesundheitsorganisation und anderen Organisationen ins Leben gerufen.\n\nQuelle: Wikipedia", Calendar.FEBRUARY, 4),
    
    /** Time shift. */
    ZeitumstellungVor(0, "Zeitumstellung (Sommerzeit)", "von 2 Uhr vor auf 3 Uhr"),
    ZeitumstellungNach(0, "Zeitumstellung (Winterzeit)", "von 3 Uhr zurück auf 2 Uhr"),
    
    /** Seasons. */
    SPRING(0, "Frühlingsanfang", "", Calendar.MARCH, 21),
    SUMMER(0, "Sommeranfang", "Astronomisch beginnt der Sommer mit der Sommersonnenwende – dem Zeitpunkt, zu dem die Sonne senkrecht über dem Wendekreis der eigenen Erdhälfte steht und die Tage am längsten sind. Zur Sommersonnenwende – auf der Nordhalbkugel der Erde am 20., 21. oder 22. Juni – erreicht die Sonne ihren mittäglichen Höchststand über dem Horizont. Auf der Südhalbkugel sind die Verhältnisse umgekehrt. Während des dortigen Winters ist auf der Nordhalbkugel Sommer.\nDie Sommersonnenwende wird in vielen Ländern, wie in Mitteleuropa und den USA, als Beginn der Jahreszeit Sommer gesehen. Ab da werden die Tage wieder kürzer.\n\nQuelle: Wikipedia", Calendar.JUNE, 21),
    AUTUMN(0, "Herbstanfang", "", Calendar.SEPTEMBER, 23),
    WINTER(0, "Winteranfang", "Astronomisch beginnt der Winter mit der Wintersonnenwende – dem Zeitpunkt, zu dem die Sonne senkrecht über dem Wendekreis der anderen Erdhälfte steht und die Tage am kürzesten sind. Zur Wintersonnenwende – auf der Nordhalbkugel der Erde am 21. oder 22. Dezember – erreicht die Sonne die geringste Mittagshöhe über dem Horizont. Auf der Südhalbkugel sind die Verhältnisse umgekehrt. Während des dortigen Winters ist auf der Nordhalbkugel Sommer.\nDa ab 21./22. Dezember die Tage wieder länger werden, war die Wintersonnenwende in vielen antiken und frühmittelalterlichen Kulturen ein wichtiges Fest, das oft ein paar Tage vor bzw. nach dem Datum der tatsächlichen Sonnenwende gefeiert wurde.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 21);
    
    /** Title and description of the holiday. */
    private final String title, description;
    
    /** In case of yearly holidays: day and month. */
    private final int day, month, bitCode;
    
    /** All yearly (static) holidays by law. */
    private static List<HolidayConstants> staticByLaw = Arrays.asList(
            NEUJAHR, HL3K, TDA, MHIMMELF, TDDE, REFORM, ALLERH, WEIH1, WEIH2);
    
    /** All flexible holidays by law. */
    private static List<HolidayConstants> flexibleByLaw = Arrays.asList(
            GRDO, KARFR, OSTERMO, CHRHIMMELF, PFINGSTMO, FRONLEICH, BUBT);
    
    /** All holidays by law. */
    private static List<HolidayConstants> allByLaw = Arrays.asList(
            NEUJAHR, HL3K, GRDO, KARFR, OSTERMO, TDA, CHRHIMMELF, PFINGSTMO,
            FRONLEICH, MHIMMELF, TDDE, REFORM, ALLERH, BUBT, WEIH1, WEIH2);
    
    /** All yearly (static) special days. */
    private static List<HolidayConstants> staticSpecial = Arrays.asList(
            VALENTIN, FRAUEN, WALPURGIS, KINDER, HALLOWEEN, MARTIN, NIKO, HEILIGA, SILVESTER);
    
    /** All flexible special days. */
    private static List<HolidayConstants> flexibleSpecial = Arrays.asList(
            ROSENM, FASCHING, ASCHERM, PALMS, MUTTER, VOLKSTRAUER, TOTENS, ADV1, ADV2, ADV3, ADV4);
    
    /** All special days. */
    private static List<HolidayConstants> allSpecial = Arrays.asList(
            VALENTIN, ROSENM, FASCHING, ASCHERM, FRAUEN, PALMS, WALPURGIS, MUTTER, KINDER, HALLOWEEN,
            MARTIN, VOLKSTRAUER, TOTENS, ADV1, NIKO, ADV2, ADV3, ADV4, HEILIGA, SILVESTER);
    
    /** All yearly (static) action days. */
    private static List<HolidayConstants> staticAction1 = Arrays.asList(
            AFRIKA, ARTENSCHUTZ, AUTOFREI, BLUTSPENDE, BODEN, BUCH, DARWIN, ANTI_DIET, DROGEN,
            EHRENAMT, ERDE, ERFINDER, ERNAEHRUNG, FAHRRAD, F_D_MUSIQUE, GESUNDHEIT, JAZZ,
            LEHRER, MAENNER, MEER, METEOROLOG, MUSIK, MAEDCHEN, NICHTRAUCH, RADIO, SPIEL),
        staticAction2 = Arrays.asList(
            AIDS, ALZHEIMER, HAND_WASCH, HEPATITS, KINDERBUCH, FERNMELDE, ANTI_KORRUP,
            KRANKE, KULTUR_ENTW, POLIO, WFMA, SPORT, SPRACHEN, TANZ, TIERSCHUTZ, TOILETTEN,
            TOURISMUS, TUBERKULOSE, UMWELT, VEGAN, VERBRAUCHER, WASSER, RHEUMA, OSTEOP,
            WIND, WISSENSCH, FEUCHT, MENSCHEN_RE, KREBS);
    
    /** All flexible action days. */
    private static List<HolidayConstants> flexibleAction1 = Arrays.asList(
            BIER, GEBET, KAUF_NIX, LACH, MUSEUM, PHILOSOPHIE),
        flexibleAction2 = Arrays.asList(
            LEPRA, SOZ_KOMM, NIEREN);
    
    /** All action days. */
    private static List<HolidayConstants> allAction1 = Arrays.asList(
            AFRIKA, ARTENSCHUTZ, AUTOFREI, BLUTSPENDE, BODEN, BUCH, DARWIN, ANTI_DIET, DROGEN,
            EHRENAMT, ERDE, ERFINDER, ERNAEHRUNG, FAHRRAD, F_D_MUSIQUE, GESUNDHEIT, JAZZ,
            LEHRER, MAENNER, MEER, METEOROLOG, MUSIK, MAEDCHEN, NICHTRAUCH, RADIO, SPIEL,
            BIER, GEBET, KAUF_NIX, LACH, MUSEUM, PHILOSOPHIE),
        allAction2 = Arrays.asList(
            AIDS, ALZHEIMER, HAND_WASCH, HEPATITS, KINDERBUCH, FERNMELDE, ANTI_KORRUP,
            KRANKE, KULTUR_ENTW, POLIO, WFMA, SPORT, SPRACHEN, TANZ, TIERSCHUTZ, TOILETTEN,
            TOURISMUS, TUBERKULOSE, UMWELT, VEGAN, VERBRAUCHER, WASSER, RHEUMA, OSTEOP,
            WIND, WISSENSCH, FEUCHT, MENSCHEN_RE, KREBS, 
            LEPRA, SOZ_KOMM, NIEREN);
    
    /**
     * Construct a holiday object with a flexible frequency.
     * @param bitCode - integer (4 byte) where 1 bit indicates the holiday by the position of the bit
     * @param title - Name of the holiday
     * @param description - Description of the holiday (can be empty)
     */
    private HolidayConstants(int bitCode, String title, String description) {
        this.bitCode = bitCode;
        this.title = title;
        this.description = description;
        this.day = -1;
        this.month = -1;
    }
    
    /**
     * Construct a holiday object with a static yearly frequency.
     * @param bitCode - integer (4 byte) where 1 bit indicates the holiday by the position of the bit
     * @param title - Name of the holiday
     * @param description - Description of the holiday (can be empty)
     * @param month - Month of the holiday (java.util.Calendar)
     * @param day - Day of the holiday in this month where the holiday occurs each year
     */
    private HolidayConstants(int bitCode, String title, String description, int month, int day) {
        this.bitCode = bitCode;
        this.title = title;
        this.description = description;
        this.day = day;
        this.month = month;
    }

    /**
     * Returns the holidays name.
     * @return the holidays name.
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Returns the holidays description (can be empty).
     * @return the holidays description (can be empty).
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Returns the holidays bit code. This is an integer (4 byte) where exactly 1 bit is set.
     * The position of the bit identifies this holiday.
     * @return the holidays bit code.
     */
    public int getBitCode() {
        return bitCode;
    }
    
    /**
     * Returns the holidays date for a given year (only for simple yearly frequency!).
     * @param year - Year for which to return the holidays date
     * @return the holidays date for the given year.
     */
    public Date getDate(int year) {
        return new Date(year, month, day);
    }
    
    /**
     * Constructs a string with the holidays title and a suffix for the holidays date (day + month
     * where possible; or "(variable)" otherwise).
     * @return a string with the holidays title and a suffix for the holidays date.
     */
    public String getTitleWithDate() {
        if (day == -1) {
            return title + " (variabel)";
        } else {
            return title + " (" + day + "." + (month + 1) + ".)";
        }
    }
    
    /**
     * Checks if the holiday is set in a given bit code.
     * @param config - Bit code to check
     * @return True if the holiday bit is set; false otherwise. 
     */
    public boolean isActive(int config) {
        return (bitCode & config) == bitCode;
    }
    
    /**
     * Returns a list of static holidays by law according to a given bit code.
     * @param bitCode - Bit code indicating which holidays are set
     * @return list of static holidays by law according to the given bit code.
     */
    public static List<HolidayConstants> getStaticByLaw(int bitCode) {
        List<HolidayConstants> ret = new ArrayList<HolidayConstants>();
        for (HolidayConstants h : staticByLaw) {
            if ((bitCode & h.getBitCode()) == h.getBitCode()) {
                ret.add(h);
            }
        }
        
        return ret;
    }
    
    /**
     * Returns a list of flexible holidays by law according to a given bit code.
     * @param bitCode - Bit code indicating which holidays are set
     * @return list of flexible holidays by law according to the given bit code.
     */
    public static List<HolidayConstants> getFlexibleByLaw(int bitCode) {
        List<HolidayConstants> ret = new ArrayList<HolidayConstants>();
        for (HolidayConstants h : flexibleByLaw) {
            if ((bitCode & h.getBitCode()) == h.getBitCode()) {
                ret.add(h);
            }
        }
        
        return ret;
    }
    
    /**
     * Returns a list of ALL holidays by law.
     * @return a list of ALL holidays by law.
     */
    public static List<HolidayConstants> getAllByLaw() {
        return allByLaw;
    }
    
    /**
     * Returns a list of static special holidays according to a given bit code.
     * @param bitCode - Bit code indicating which holidays are set
     * @return list of static special holidays according to the given bit code.
     */
    public static List<HolidayConstants> getStaticSpecial(int bitCode) {
        List<HolidayConstants> ret = new ArrayList<HolidayConstants>();
        for (HolidayConstants h : staticSpecial) {
            if ((bitCode & h.getBitCode()) == h.getBitCode()) {
                ret.add(h);
            }
        }
        
        return ret;
    }
    
    /**
     * Returns a list of flexible special holidays according to a given bit code.
     * @param bitCode - Bit code indicating which holidays are set
     * @return list of flexible special holidays according to the given bit code.
     */
    public static List<HolidayConstants> getFlexibleSpecial(int bitCode) {
        List<HolidayConstants> ret = new ArrayList<HolidayConstants>();
        for (HolidayConstants h : flexibleSpecial) {
            if ((bitCode & h.getBitCode()) == h.getBitCode()) {
                ret.add(h);
            }
        }
        
        return ret;
    }
    
    /**
     * Returns a list of ALL special holidays.
     * @return a list of ALL special holidays.
     */
    public static List<HolidayConstants> getAllSpecial() {
        return allSpecial;
    }
    
    /**
     * Returns a list of static action/world days according to given bit codes.
     * @param bitCode1 - Bit code 1 indicating which action/world days are set
     * @param bitCode2 - Bit code 2 indicating which action/world days are set
     * @return list of static action/world days according to the given bit code.
     */
    public static List<HolidayConstants> getStaticAction(int bitCode1, int bitCode2) {
        List<HolidayConstants> ret = new ArrayList<HolidayConstants>();
        for (HolidayConstants h : staticAction1) {
            if ((bitCode1 & h.getBitCode()) == h.getBitCode()) {
                ret.add(h);
            }
        }
        for (HolidayConstants h : staticAction2) {
            if ((bitCode2 & h.getBitCode()) == h.getBitCode()) {
                ret.add(h);
            }
        }
        
        return ret;
    }
    
    /**
     * Returns a list of flexible action/world days according to given bit codes.
     * @param bitCode1 - Bit code 1 indicating which action/world days are set
     * @param bitCode2 - Bit code 2 indicating which action/world days are set
     * @return list of flexible action/world days according to the given bit code.
     */
    public static List<HolidayConstants> getFlexibleAction(int bitCode1, int bitCode2) {
        List<HolidayConstants> ret = new ArrayList<HolidayConstants>();
        for (HolidayConstants h : flexibleAction1) {
            if ((bitCode1 & h.getBitCode()) == h.getBitCode()) {
                ret.add(h);
            }
        }
        for (HolidayConstants h : flexibleAction2) {
            if ((bitCode2 & h.getBitCode()) == h.getBitCode()) {
                ret.add(h);
            }
        }
        
        return ret;
    }
    
    /**
     * Returns a list of ALL action/world days from list 1.
     * @return a list of ALL action/world days from list 1.
     */
    public static List<HolidayConstants> getAllAction1() {
        return allAction1;
    }
    
    /**
     * Returns a list of ALL action/world days from list 2.
     * @return a list of ALL action/world days from list 2.
     */
    public static List<HolidayConstants> getAllAction2() {
        return allAction2;
    }
    
    /**
     * Returns the holiday identified by the title.
     * @param title - Name of the holiday to search
     * @return the holiday identified by the title.
     */
    public static HolidayConstants getByName(String title) {
        HolidayConstants[] all = HolidayConstants.values();
        for (HolidayConstants h : all) {
            if (h.getTitle().equals(title)) {
                return h;
            }
        }
        return null;
    }
    
    /**
     * Returns the default bit code with some default holidays by law set.
     * @return the default bit code with some default holidays by law set.
     */
    public static int getDefaultByLaw() {
        return NEUJAHR.bitCode 
                | KARFR.bitCode 
                | OSTERMO.bitCode 
                | TDA.bitCode
                | CHRHIMMELF.bitCode 
                | PFINGSTMO.bitCode 
                | TDDE.bitCode
                | WEIH1.bitCode 
                | WEIH2.bitCode;
    }
    
    /**
     * Returns the default bit code with some default special holidays set.
     * @return the default bit code with some default special holidays set.
     */
    public static int getDefaultSpecial() {
        return VALENTIN.bitCode
                | WALPURGIS.bitCode
                | MUTTER.bitCode
                | KINDER.bitCode
                | HALLOWEEN.bitCode
                | NIKO.bitCode
                | ADV1.bitCode
                | ADV2.bitCode
                | ADV3.bitCode
                | ADV4.bitCode
                | HEILIGA.bitCode
                | SILVESTER.bitCode;
    }
    
    /**
     * Returns the default bit code with the default action/world days from list 1.
     * @return the default bit code with the default action/world days from list 1.
     */
    public static int getDefaultAction1() {
        return 0;
    }
    
    /**
     * Returns the default bit code with the default action/world days from list 2.
     * @return the default bit code with the default action/world days from list 2.
     */
    public static int getDefaultAction2() {
        return 0;
    }
}

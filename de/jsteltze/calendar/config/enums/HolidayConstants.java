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
    HL3K         (0x00000002, "Heilige drei K�nige",        "In vielen Gebieten Deutschlands ist der Begriff \"Dreik�nigsfest\" oder \"Dreik�nigstag\" der vorherrschend gebrauchte Name f�r den 6. Januar.\nDie eigentliche Bezeichnung dieses Festes ist dagegen \"Erscheinung des Herrn\". Die Kirche feiert an diesem Tag das Sichtbarwerden der G�ttlichkeit Jesu in der Anbetung durch die Magier, bei seiner Taufe im Jordan und durch das von ihm bei der Hochzeit zu Kana bewirkte Wunder der Verwandlung von Wasser in Wein.\n\nIn Deutschland ist der 6. Januar gesetzlicher Feiertag nur in Baden-W�rttemberg, Bayern und Sachsen-Anhalt.\n\nQuelle: Wikipedia", Calendar.JANUARY, 6),
    GRDO         (0x00000004, "Gr�ndonnerstag",             "Gr�ndonnerstag (auch Hoher, Heiliger oder Wei�er Donnerstag bzw. Palmdonnerstag) ist die deutschsprachige Bezeichnung f�r den f�nften Tag der Karwoche bzw. der Heiligen Woche. An ihm gedenken die Kirchen des letzten Abendmahles Jesu mit den zw�lf Aposteln am Vorabend seiner Kreuzigung.\n\nGr�ndonnerstag ist kein gesetzlicher Feiertag in Deutschland.\n\nQuelle: Wikipedia"),
    KARFR        (0x00000008, "Karfreitag",                 "Der Karfreitag (althochdeutsch kara 'Klage', 'Kummer', 'Trauer') ist der Freitag vor Ostern. Christen gedenken an diesem Tag des Kreuzestodes Jesu Christi. In der katholischen Kirche ist er ein strenger Fast- und Abstinenztag.\n\nKarfreitag ist ein gesetzlicher Feiertag in Deutschland.\n\nQuelle: Wikipedia"),
    OSTERMO      (0x00000010, "Ostermontag",                "Der Ostermontag geh�rt mit dem 26. Dezember und dem Pfingstmontag zu den zweiten Feiertagen der h�chsten christlichen Feste.\nEr steht als zweiter Feiertag ganz im Zeichen des Osterfestes. Nach der Verk�ndigung des Neuen Testaments (NT) wurde Jesus Christus, Sohn Gottes, am dritten Tag nach seiner Kreuzigung von den Toten erweckt und erschien seinen J�ngerinnen und J�ngern in leiblicher Gestalt.\n\nOstermontag ist ein gesetzlicher Feiertag in Deutschland.\n\nQuelle: Wikipedia"),
    TDA          (0x00000020, "Tag der Arbeit",             "Der Erste Mai wird als \"Tag der Arbeit\", \"Tag der Arbeiterbewegung\", \"Internationaler Kampftag der Arbeiterklasse\" oder auch als Maifeiertag bezeichnet. Er ist in Deutschland, Liechtenstein, �sterreich, Belgien, Teilen der Schweiz und vielen anderen Staaten ein gesetzlicher Feiertag.\n\nQuelle: Wikipedia", Calendar.MAY, 1),
    CHRHIMMELF   (0x00000040, "Christi Himmelfahrt",        "Christi Himmelfahrt (altgr. \"die Aufnahme des Herrn\", lat. \"Aufstieg des Herrn\"), bezeichnet im christlichen Glauben die R�ckkehr Jesu Christi als Sohn Gottes zu seinem Vater in den Himmel. Christi Himmelfahrt wird am 40. Tag des Osterfestkreises, also 39 Tage nach dem Ostersonntag, gefeiert. Deshalb f�llt das Fest immer auf einen Donnerstag\n\nChristi Himmelfahrt ist ein gesetzlicher Feiertag in Deutschland.\n\nQuelle: Wikipedia"),
    PFINGSTMO    (0x00000080, "Pfingstmontag",              "Pfingsten (altgr. \"f�nfzigster Tag\") ist ein christliches Fest. Am 50. Tag des Osterfestkreises, also 49 Tage nach dem Ostersonntag, wird von den Gl�ubigen die Entsendung des Heiligen Geistes gefeiert.\n\nPfingstmontag ist ein gesetzlicher Feiertag in Deutschland.\n\nQuelle: Wikipedia"),
    FRONLEICH    (0x00000100, "Fronleichnahm",              "Das Fronleichnamsfest oder Fest des heiligsten Leibes und Blutes Christi ist ein Hochfest im Kirchenjahr der katholischen Kirche, mit dem die leibliche Gegenwart Jesu Christi im Sakrament der Eucharistie gefeiert wird.\n\nIn Deutschland ist Fronleichnam ein gesetzlicher Feiertag in den L�ndern Baden-W�rttemberg, Bayern, Hessen, Nordrhein-Westfalen, Rheinland-Pfalz und im Saarland sowie in einigen Gemeinden mit �berwiegend katholischer Bev�lkerung in den L�ndern Sachsen und Th�ringen.\n\nQuelle: Wikipedia"),
    MHIMMELF     (0x00000200, "Mari� Himmelfahrt",          "Mari� Aufnahme in den Himmel, auch Mari� Himmelfahrt oder Vollendung Mariens ist ein Hochfest der r�misch-katholischen Kirche am 15. August. Der Glaube an die leibliche Aufnahme Mariens in den Himmel ist seit dem 6. Jahrhundert bezeugt und wurde 1950 von Papst Pius XII. f�r die r�misch-katholische Kirche zum Dogma erhoben.\n\nMari� Himmelfahrt (nicht zu verwechseln mit Christi Himmelfahrt) ist in Saarland ein gesetzlicher Feiertag.\n\nQuelle: Wikipedia", Calendar.AUGUST, 15),
    TDDE         (0x00000400, "Tag der deutschen Einheit",  "Der 3. Oktober wurde als Tag der Deutschen Einheit im Einigungsvertrag 1990 zum gesetzlichen Feiertag in Deutschland bestimmt. Als deutscher Nationalfeiertag erinnert er an die deutsche Wiedervereinigung, die mit dem Wirksamwerden des Beitritts der Deutschen Demokratischen Republik zur Bundesrepublik Deutschland am 3. Oktober 1990 vollendet wurde.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 3), 
    REFORM       (0x00000800, "Reformationstag",            "Der Reformationstag, das Reformationsfest oder auch der Gedenktag der Reformation wird von evangelischen Christen in Deutschland und �sterreich am 31. Oktober im Gedenken an die Reformation der Kirche durch Martin Luther gefeiert.\n\nDer Reformationstag ist seit der deutschen Wiedervereinigung gesetzlicher Feiertag in den deutschen L�ndern Brandenburg, Mecklenburg-Vorpommern, Sachsen, Sachsen-Anhalt und Th�ringen.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 31),
    ALLERH       (0x00001000, "Allerheiligen",              "Allerheiligen ist ein christliches Fest, zu dem aller Heiligen gedacht wird - auch solcher, die nicht heiliggesprochen wurden - sowie der vielen Heiligen, um deren Heiligkeit niemand wei� als Gott.\n\nAllerheiligen ist ein gesetzlicher Feiertag in den katholisch gepr�gten deutschen Bundesl�ndern Baden-W�rttemberg, Bayern, Nordrhein-Westfalen, Rheinland-Pfalz und Saarland.\n\nQuelle: Wikipedia", Calendar.NOVEMBER, 1),
    BUBT         (0x00002000, "Bu�- und Bettag",            "Der Bu�- und Bettag in Deutschland ist ein Feiertag der evangelischen Kirche.  Angesichts von Notst�nden und Gefahren wurde die ganze Bev�lkerung zu Umkehr und Gebet aufgerufen. Seit Ende des 19. Jahrhunderts wird ein allgemeiner Bu�- und Bettag am Mittwoch vor dem Ewigkeitssonntag, dem letzten Sonntag des Kirchenjahres, begangen, also elf Tage vor dem ersten Adventssonntag bzw. am Mittwoch vor dem 23. November.\nIm Jahr 1994 wurde beschlossen, den Bu�- und Bettag als arbeitsfreien Tag mit Wirkung ab 1995 zu streichen, um die Mehrbelastung f�r die Arbeitgeber durch die Beitr�ge zur neu eingef�hrten Pflegeversicherung durch Mehrarbeit der Arbeitnehmer auszugleichen.\nLediglich im Freistaat Sachsen besteht er bis heute als gesetzlicher Feiertag weiter. Daf�r bezahlen in Sachsen abh�ngig Besch�ftigte (nicht jedoch deren Arbeitgeber) einen h�heren Beitrag zur Pflegeversicherung als im restlichen Bundesgebiet.\n\nQuelle: Wikipedia"),
    WEIH1        (0x00004000, "1. Weihnachtsfeiertag",      "Weihnachten, auch Weihnacht, Christfest oder Heiliger Christ genannt, ist das Fest der Geburt Jesu Christi. Festtag ist der 25. Dezember, der Christtag, auch Hochfest der Geburt des Herrn, dessen Feierlichkeiten am Vorabend, dem Heiligen Abend, beginnen. Er ist in vielen Staaten ein gesetzlicher Feiertag.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 25),
    WEIH2        (0x00008000, "2. Weihnachtsfeiertag",      "Der 26. Dezember ist als Zweiter Weihnachtsfeiertag oder Stephanstag ein gesetzlicher Feiertag in den meisten europ�ischen L�ndern. Er geh�rt zu den Feiertagen zwischen den Jahren.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 26),
    
    /** Special holidays. */
    VALENTIN     (0x00000001, "Valentinstag",               "Der Valentinstag am 14. Februar gilt in einigen L�ndern als Tag der Liebenden. Das Brauchtum dieses Tages geht auf einen oder mehrere christliche M�rtyrer namens Valentinus zur�ck, die der �berlieferung zufolge das Martyrium durch Enthaupten erlitten haben.\n\nAn Bekanntheit gewann der Valentinstag im deutschen Sprachraum durch den Handel mit Blumen, besonders jedoch durch die intensive Werbung der Blumenh�ndler und S��warenfabrikanten.\n\nQuelle: Wikipedia", Calendar.FEBRUARY, 14),
    ROSENM       (0x00000002, "Rosenmontag",                "Der Rosenmontag wird insbesondere im rheinischen Karneval als H�hepunkt der Karnevalszeit, oft mit dem so genannten Rosenmontagszug, begangen. Er f�llt auf Montag vor dem Aschermittwoch; 48 Tage vor dem Ostersonntag. In sogenannten Karnevalshochburgen wie unter anderem K�ln, D�sseldorf oder Mainz geben am Rosenmontag als Brauchtumstag viele Arbeitgeber ihren Mitarbeitern zwar frei, ein gesetzlicher Feiertag ist der Rosenmontag aber in keinem Bundesland.\nDer Rosenmontag liegt zwischen dem Karnevalssonntag (Fastnachtssonntag) und dem Faschingsdienstag.\n\nQuelle: Wikipedia"),
    FASCHING     (0x00000004, "Faschingsdienstag",          "Der Fastnachtsdienstag (auch Faschingsdienstag, Karnevalsdienstag, oder Veilchendienstag) ist die Bezeichnung f�r den letzten der Karnevalstage, den Tag zwischen Rosenmontag und Aschermittwoch. Als letzter Tag vor dem Beginn der Fastenzeit kommt ihm in vielen Regionen eine besondere Bedeutung zu. In einigen Regionen, in denen Karneval gefeiert wird, stellt er den H�hepunkt der Feiertage dar.\nInternational ist dieser Tag auch als Mardi Gras (�Fetter Dienstag�) oder Shrove oder Pancake (Tues)day bekannt.\nUm Mitternacht in der Nacht zum Aschermittwoch gibt es zahlreiche Rituale, mit denen die Karnevalsfeierlichkeiten beendet werden. Hierzu geh�ren die Nubbelverbrennungen, aber auch die symbolische Beerdigung von Karneval, Fastnacht oder Fasching.\n\nQuelle: Wikipedia"),
    ASCHERM      (0x00000008, "Aschermittwoch",             "Der Aschermittwoch stellt in der Westkirche seit dem Pontifikat Gregors des Gro�en den Beginn der 40-t�gigen Fastenzeit dar. Es ist der Tag nach dem Fastnachtsdienstag.\nDie Bezeichnung Aschermittwoch kommt von dem Brauch, in der Heiligen Messe dieses Tages die Asche vom Verbrennen der Palmzweige des Vorjahres zu segnen und die Gl�ubigen mit einem Kreuz aus dieser Asche zu bezeichnen. Der Empfang des Aschenkreuzes geh�rt zu den heilswirksamen Zeichen, den Sakramentalien.\n\nQuelle: Wikipedia"),
    FRAUEN       (0x00000010, "Frauentag",                  "Der Internationale Frauentag, Weltfrauentag, Frauenkampftag, Internationaler Frauenkampftag oder Frauentag ist ein Welttag, der am 8. M�rz begangen wird. Er entstand als Initiative sozialistischer Organisationen in der Zeit um den Ersten Weltkrieg im Kampf um die Gleichberechtigung, das Wahlrecht f�r Frauen und die Emanzipation von Arbeiterinnen. Die Vereinten Nationen erkoren ihn sp�ter als Tag der Vereinten Nationen f�r die Rechte der Frau und den Weltfrieden aus.\n\nQuelle: Wikipedia", Calendar.MARCH, 8),
    PALMS        (0x00000020, "Palmsonntag",                "Der Palmsonntag ist der sechste und letzte Sonntag der Fastenzeit und der Sonntag vor Ostern. Mit dem Palmsonntag beginnt die Karwoche, die in der evangelisch-lutherischen Kirche auch Stille Woche genannt wird. Die Gro�e Woche bzw. Heilige Woche der katholischen und der orthodoxen Tradition umfasst dar�ber hinaus auch Ostern.\n\nQuelle: Wikipedia"),
    MUTTER       (0x00000040, "Muttertag",                  "Der Muttertag ist ein Tag zu Ehren der Mutter und der Mutterschaft. Er hat sich seit 1914, beginnend in den Vereinigten Staaten, in der westlichen Welt etabliert. Im deutschsprachigen Raum und vielen anderen L�ndern wird er am zweiten Sonntag im Mai begangen.\n\nQuelle: Wikipedia"),
    KINDER       (0x00000080, "Kindertag",                  "Der Kindertag, auch Weltkindertag, internationaler Kindertag oder internationaler Tag des Kindes, ist ein in �ber 145 Staaten der Welt begangener Tag, um auf die besonderen Bed�rfnisse der Kinder und speziell auf die Kinderrechte aufmerksam zu machen.\nDie Art seiner Ausrichtung reicht von einem Gedenk- bzw. Ehrentag f�r Kinder �ber einen Quasi-Feiertag mit Festen und Geschenken bis zu politischen Pressemitteilungen, Aktionen und Demonstrationen in der Tradition eines Kampftages.\nZiel des Tages sind Themen wie Kinderschutz, Kinderpolitik und vor allem die Kinderrechte in das �ffentliche Bewusstsein zu r�cken.\n\nEs gibt kein international einheitliches Datum, was historisch begr�ndet ist. In �ber 40 Staaten wie in China, in den USA (teilweise), vielen mittel- und osteurop�ischen L�ndern sowie Nachfolgestaaten der Sowjetunion wird am 1. Juni der internationale Kindertag begangen.\n\nQuelle: Wikipedia", Calendar.JUNE, 1),
    HALLOWEEN    (0x00000100, "Halloween",                  "Halloween benennt die Volksbr�uche am Abend und in der Nacht vor dem Hochfest Allerheiligen, vom 31. Oktober auf den 1. November. Dieses Brauchtum war urspr�nglich vor allem im katholischen Irland verbreitet. Die irischen Einwanderer in den USA pflegten ihre Br�uche in Erinnerung an die Heimat und bauten sie aus.\nSeit den 1990er Jahren verbreiten sich Halloween-Br�uche in US-amerikanischer Auspr�gung auch im kontinentalen Europa.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 31),
    MARTIN       (0x00000200, "Martinstag",                 "Der Martinstag am 11. November (in Altbayern und �sterreich auch 'Martini') ist im Kirchenjahr das Fest des heiligen Martin von Tours. Das Datum des gebotenen Gedenktags im r�mischen Generalkalender, das sich auch in orthodoxen Heiligenkalendern, im evangelischen Namenkalender und dem anglikanischen Common Worship findet, ist von der Grablegung des heiligen Martin am 11. November 397 abgeleitet. Der Martinstag ist in Mitteleuropa von zahlreichen Br�uchen gepr�gt, darunter das Martinsgansessen, der Martinszug und das Martinssingen.\n\nQuelle: Wikipedia", Calendar.NOVEMBER, 11),
    VOLKSTRAUER  (0x00000400, "Volkstrauertag",             "Der Volkstrauertag ist in Deutschland ein staatlicher Gedenktag und geh�rt zu den sogenannten stillen Tagen. Er wird seit 1952 zwei Sonntage vor dem ersten Adventssonntag begangen und erinnert an die Kriegstoten und Opfer der Gewaltherrschaft aller Nationen.\n\nQuelle: Wikipedia"),
    TOTENS       (0x00000800, "Totensonntag",               "Der Totensonntag oder Ewigkeitssonntag ist in den evangelischen Kirchen in Deutschland und der Schweiz ein Gedenktag f�r die Verstorbenen. Er ist der letzte Sonntag vor dem ersten Adventssonntag und damit der letzte Sonntag des Kirchenjahres.\n\nSeit der Entwicklung des Kirchenjahres im Mittelalter wurden mit den letzten Sonntagen des Kirchenjahres liturgische Lesungen zu den Letzten Dingen verbunden. W�hrend am drittletzten Sonntag das Thema �Tod� im Mittelpunkt steht, hat der vorletzte Sonntag die Thematik �(J�ngstes) Gericht� und der letzte �Ewiges Leben�.\n\nQuelle: Wikipedia"),
    ADV1         (0x00001000, "1. Advent",                  "Advent (lateinisch adventus �Ankunft�), eigentlich Adventus Domini (lat. f�r Ankunft des Herrn), bezeichnet die Jahreszeit, in der die Christenheit sich auf das Fest der Geburt Jesu Christi, Weihnachten, vorbereitet. Die Christen gedenken der Geburt Jesu und feiern sie als Menschwerdung Gottes. Zugleich erinnert der Advent daran, dass Christen das zweite Kommen Jesu Christi erwarten sollen. Mit dem ersten Adventssonntag beginnt f�r die r�misch-katholische Kirche und die evangelische Kirche auch das neue Kirchenjahr.\n\nQuelle: Wikipedia"),
    NIKO         (0x00002000, "Nikolaus",                   "Nikolaus von Myra (geb. zw. 270 und 286 in Patara; gest. 6. Dezember zw. 326 und 365) ist einer der bekanntesten Heiligen der Ostkirchen und der lateinischen Kirche. Sein Gedenktag, der 6. Dezember, wird im gesamten Christentum mit zahlreichen Volksbr�uchen begangen.\nNikolaus wirkte in der ersten H�lfte des 4. Jahrhunderts als Bischof von Myra in der kleinasiatischen Region Lykien, damals Teil des r�mischen, sp�ter des byzantinischen Reichs, heute der T�rkei.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 6),
    ADV2         (0x00004000, "2. Advent",                  "Advent (lateinisch adventus �Ankunft�), eigentlich Adventus Domini (lat. f�r Ankunft des Herrn), bezeichnet die Jahreszeit, in der die Christenheit sich auf das Fest der Geburt Jesu Christi, Weihnachten, vorbereitet. Die Christen gedenken der Geburt Jesu und feiern sie als Menschwerdung Gottes. Zugleich erinnert der Advent daran, dass Christen das zweite Kommen Jesu Christi erwarten sollen. Mit dem ersten Adventssonntag beginnt f�r die r�misch-katholische Kirche und die evangelische Kirche auch das neue Kirchenjahr.\n\nQuelle: Wikipedia"),
    ADV3         (0x00008000, "3. Advent",                  "Advent (lateinisch adventus �Ankunft�), eigentlich Adventus Domini (lat. f�r Ankunft des Herrn), bezeichnet die Jahreszeit, in der die Christenheit sich auf das Fest der Geburt Jesu Christi, Weihnachten, vorbereitet. Die Christen gedenken der Geburt Jesu und feiern sie als Menschwerdung Gottes. Zugleich erinnert der Advent daran, dass Christen das zweite Kommen Jesu Christi erwarten sollen. Mit dem ersten Adventssonntag beginnt f�r die r�misch-katholische Kirche und die evangelische Kirche auch das neue Kirchenjahr.\n\nQuelle: Wikipedia"),
    ADV4         (0x00010000, "4. Advent",                  "Advent (lateinisch adventus �Ankunft�), eigentlich Adventus Domini (lat. f�r Ankunft des Herrn), bezeichnet die Jahreszeit, in der die Christenheit sich auf das Fest der Geburt Jesu Christi, Weihnachten, vorbereitet. Die Christen gedenken der Geburt Jesu und feiern sie als Menschwerdung Gottes. Zugleich erinnert der Advent daran, dass Christen das zweite Kommen Jesu Christi erwarten sollen. Mit dem ersten Adventssonntag beginnt f�r die r�misch-katholische Kirche und die evangelische Kirche auch das neue Kirchenjahr.\n\nQuelle: Wikipedia"),
    HEILIGA      (0x00020000, "Heiligabend",                "Der Heilige Abend am 24. Dezember, auch Heiligabend oder Weihnachtsabend genannt, ist der Vorabend des Weihnachtsfestes; vielerorts wird auch der ganze Vortag so bezeichnet. Am Abend findet unter anderem in Deutschland, der Schweiz, in Liechtenstein und in �sterreich traditionell die Bescherung statt. Als Heilige Nacht oder als Christnacht wird die Nacht vom 24. auf den 25. Dezember bezeichnet.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 24),
    SILVESTER    (0x00040000, "Silvester",                  "Als Silvester wird in einigen europ�ischen Sprachen der 31. Dezember, der letzte Tag des Jahres im westlichen Kulturraum, bezeichnet. Nach dem r�misch-katholischen Heiligenkalender ist Papst Silvester I. (gest. 31. Dezember 335) der Tagesheilige. Auf Silvester folgt mit dem Neujahrstag der 1. Januar des folgenden Jahres.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 31),
    WALPURGIS    (0x00080000, "Walpurgisnacht",             "Die Walpurgisnacht (auch Hexenbrennen) ist ein traditionelles nord- und mitteleurop�isches Fest am 30. April. Der Name leitet sich von der heiligen Walburga ab, deren Gedenktag bis ins Mittelalter am Tag ihrer Heiligsprechung am 1. Mai gefeiert wurde. Die Walpurgisnacht war die Vigilfeier des Fests. Als �Tanz in den Mai� hat sie wegen der Gelegenheit zu Tanz und Geselligkeit am Vorabend des Maifeiertags auch als urbanes, modernes Festereignis Eingang in private und kommerzielle Veranstaltungen gefunden.\n\nQuelle: Wikipedia", Calendar.APRIL, 30),
    
    /** World days / memorial days, part 1. */
    AFRIKA       (0x00000001, "Afrikatag",                  "Als Afrikatag wird in der katholischen Kirche seit �ber hundert Jahren der 6. Januar begangen.\nAn diesem Tag werden weltweit Spenden f�r Afrika, speziell f�r die Ausbildung und F�rderung von Priestern, Ordensleuten und Katecheten, gesammelt.\n\nDiese Afrika-Kollekte wurde erstmals am 6. Januar 1891 auf Wunsch von Papst Leo XIII. durchgef�hrt, der damit Gelder zur Unterst�tzung des Kampfes gegen die Sklaverei in Afrika sammeln wollte. Es ist der �lteste Tag einer gesamtkirchlichen Missionskollekte der katholischen Kirche. Der Tag der Erscheinung des Herrn wurde gew�hlt, weil Afrika in der Gestalt des schwarzen K�nigs seit �ltester Zeit in der Tradition sichtbar vertreten ist.\n\nQuelle: Wikipedia", Calendar.JANUARY, 6),
    ARTENSCHUTZ  (0x00000002, "Tag des Artenschutzes",      "Der Tag des Artenschutzes (UN World Wildlife Day) ist ein im Rahmen des Washingtoner Artenschutz�bereinkommens (CITES, Convention on International Trade in Endangered Species of Wild Fauna and Flora) eingef�hrter Aktions- und Gedenktag. Er findet j�hrlich am 3. M�rz statt: Durch das am 3. M�rz 1973 unterzeichnete Abkommen sollen bedrohte wildlebende Arten (Tiere und Pflanzen) gesch�tzt werden, die durch Handelsinteressen gef�hrdet sind.\n\nQuelle: Wikipedia", Calendar.MARCH, 3),
    AUTOFREI     (0x00000004, "Autofreier Tag",             "Der Autofreie Tag ist ein Aktionstag, der in Europa von verschiedenen Organisationen (zum Beispiel Umweltverb�nden und Kirchen) initiiert und unterst�tzt wird. Er findet j�hrlich am 22. September statt. In Kommunen, die an der Europ�ischen Woche der Mobilit�t teilnehmen, wird von diesem Datum jedoch auch gelegentlich um wenige Tage abgewichen.\n\nDer Gedanke, einen Tag pro Jahr generell auf den Gebrauch des Autos zu verzichten, wird bereits von fast allen Staaten der Europ�ischen Union und dar�ber hinaus von den meisten Kommunen und St�dten unterst�tzt. Tausende Gemeinden in Deutschland, hunderte in der Schweiz und in �sterreich und ebenso in anderen L�ndern haben entsprechende Aufrufe erlassen.\n\nQuelle: Wikipedia", Calendar.SEPTEMBER, 22),
    BLUTSPENDE   (0x00000008, "Weltblutspendetag",          "Der Weltblutspendetag (engl. World Blood Donor Day, in Deutschland auch Weltblutspendertag genannt) wird am 14. Juni, dem Geburtstag von Karl Landsteiner (1868�1943), dem Entdecker der Blutgruppen, begangen.\n\nVier internationale Organisationen, die sich weltweit f�r sicheres Blut auf der Basis freiwilliger und unentgeltlicher Blutspenden einsetzen, haben den Tag im Jahr 2004 ausgerufen: die Weltgesundheitsorganisation (WHO), die Internationale Organisation der Rotkreuz- und Rothalbmondgesellschaften (IFRK), die Internationale Gesellschaft f�r Transfusionsmedizin (ISBT) und die Internationale F�deration der Blutspendeorganisationen (FIODS).\n\nQuelle: Wikipedia", Calendar.JUNE, 14),
    BODEN        (0x00000010, "Weltbodentag",               "Der Weltbodentag (engl. World Soil Day) ist ein internationaler Aktionstag am 5. Dezember. Er wurde von der Internationalen Bodenkundlichen Union (IUSS) im Rahmen ihres 17. Weltkongresses im August 2002 in Bangkok ernannt. Mit ihm soll ein j�hrliches Zeichen f�r die Bedeutung der nat�rlichen Ressource Boden gesetzt und f�r den Bodenschutz geworben werden.\n\nDer Boden des Jahres wird jedes Jahr am Weltbodentag f�r das folgende Jahr ausgerufen.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 5),
    BUCH         (0x00000020, "Welttag des Buches",         "Der Welttag des Buches und des Urheberrechts (kurz Weltbuchtag, englisch World Book and Copyright Day) am 23. April ist seit 1995 ein von der UNESCO weltweit eingerichteter Feiertag f�r das Lesen, f�r B�cher, f�r die Kultur des geschriebenen Wortes und auch f�r die Rechte ihrer Autoren.\n\nQuelle: Wikipedia", Calendar.APRIL, 23),
    DARWIN       (0x00000040, "Darwin-Tag",                 "Der Darwin-Tag ist ein weltweit gefeierter Gedenktag und wird j�hrlich am 12. Februar, dem Geburtstag Charles Darwins, begangen.\n\nDer Darwin-Tag versteht sich als Hommage an Darwins Beitrag zur Wissenschaft. Er soll der �ffentlichkeit auch generell die Naturwissenschaften n�herbringen (promote public education about science) und die Naturwissenschaften und die Menschheit feiern.\n\nQuelle: Wikipedia", Calendar.FEBRUARY, 12),
    ANTI_DIET    (0x00000080, "Internationaler Anti-Di�t-Tag", "Der Internationale Anti-Di�t-Tag (englisch International No Diet Day � INDD abgek�rzt) ist ein inoffizieller internationaler Aktionstag. Er wurde von der britischen Buchautorin und Feministin Mary Evans Young ins Leben gerufen und findet j�hrlich am 6. Mai statt.\n\nDie Urspr�nge dieses Aktionstages liegen im Jahr 1992. Die von einer Anorexie geheilte Mary Evans Young gr�ndete die Anti-Di�ten-Kampagne Diet Breakers. Sie erhielt mediale Aufmerksamkeit, nachdem sie sich in Talkshows und TV-Interviews f�r die Akzeptanz des eigenen K�rpers und gegen den Schlankheitswahn einsetzte. Als Antrieb gab Young sowohl ihr eigenes Schicksal als auch Medienberichte �ber Magenverkleinerungen und m�gliche Komplikationen sowie Berichte �ber jugendliche M�dchen an, die Selbstmord begangen h�tten, �weil sie es nicht mehr ertragen konnten, fett zu sein.�\n\nQuelle: Wikipedia", Calendar.MAY, 6),
    DROGEN       (0x00000100, "Weltdrogentag",              "Der �Weltdrogentag�, offiziell International Day against Drug Abuse and Illicit Trafficking oder Internationaler Tag gegen Drogenmissbrauch und unerlaubten Suchtstoffverkehr findet j�hrlich am 26. Juni statt. Dieser Aktionstag wurde im Dezember 1987 von der Generalversammlung der Vereinten Nationen festgelegt und ist gegen den Missbrauch von Drogen gerichtet. �hnlich wie der Weltnichtrauchertag ist der Weltdrogentag jedes Jahr Anlass f�r Aktionen und Pressemitteilungen. Seitens der Vereinten Nationen ist das United Nations Office on Drugs and Crime (UNODC) f�r den �Weltdrogentag� verantwortlich.\n\nQuelle: Wikipedia", Calendar.JUNE, 26),
    EHRENAMT     (0x00000200, "Internationaler Tag des Ehrenamtes", "Der Internationale Tag des Ehrenamtes (englisch International Volunteer Day for Economic and Social Development, IVD) ist ein j�hrlich am 5. Dezember abgehaltener Gedenk- und Aktionstag zur Anerkennung und F�rderung ehrenamtlichen Engagements. Er wurde 1985 von der UN mit Wirkung ab 1986 beschlossen. In Deutschland ersetzt er de facto den Tag des Ehrenamts, der fr�her am 2. Dezember begangen wurde. An diesem Tag wird auch der Verdienstorden der Bundesrepublik Deutschland an besonders engagierte Personen vergeben.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 5),
    ERDE         (0x00000400, "Tag der Erde",               "Der Tag der Erde (englisch Earth Day) wird allj�hrlich am 22. April in �ber 175 L�ndern begangen und soll die Wertsch�tzung f�r die nat�rliche Umwelt st�rken, aber auch dazu anregen, die Art des Konsumverhaltens zu �berdenken.\n\nQuelle: Wikipedia", Calendar.APRIL, 22),
    ERFINDER     (0x00000800, "Tag der Erfinder",           "Der Tag der Erfinder wird in verschiedenen L�ndern an unterschiedlichen Tagen gefeiert. In Deutschland, �sterreich und der Schweiz ist am 9. November, dem Geburtstag der Erfinderin und Hollywoodschauspielerin Hedy Lamarr, der Tag der Erfinder.\nLaut Internetpr�sentation verfolgen die Veranstalter mit dem Tag der Erfinder folgende Ziele:\n - Mut zu eigenen Ideen und zur Ver�nderung\n - Erinnern an vergessene Erfinder\n - Erinnern an gro�e Erfinder, die unser Leben verbessert haben\n - Den Ruf zeitgen�ssischer Erfinder und Vision�re verbessern\n - Zur Mitarbeit an unserer Zukunft aufrufen\n\nQuelle: Wikipedia", Calendar.NOVEMBER, 9),
    ERNAEHRUNG   (0x00001000, "Weltern�hrungstag",          "Der Weltern�hrungstag (auch Welthungertag) findet jedes Jahr am 16. Oktober statt und soll darauf aufmerksam machen, dass weltweit �ber eine Milliarde Menschen an Hunger leiden.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 16),
    FAHRRAD      (0x00002000, "Eurpo�ischer Tag des Fahrrads", "Der Europ�ische Tag des Fahrrades ist ein Aktionstag in einigen europ�ischen L�ndern und findet seit 1998 j�hrlich am 3. Juni statt.\nEr wurde anl�sslich der zunehmenden problembehafteten Verkehrsdichte durch motorisierte Fortbewegungsmittel eingef�hrt und soll darauf hinweisen, dass das Fahrrad das umweltfreundlichste, ges�ndeste und sozial vertr�glichste Fortbewegungsmittel darstellt. So finden an diesem Datum beziehungsweise Anfang Juni mit Blick auf diesen Tag verschiedene Aktionen und Sternfahrten statt. Das Statistische Bundesamt stellt anl�sslich des Tages aktuelle Statistiken zur Fahrradnutzung in Deutschland aus.\n\nQuelle: Wikipedia", Calendar.JUNE, 3),
    F_D_MUSIQUE  (0x00004000, "F�te de la Musique",         "Die F�te de la Musique (franz�sisch f�r �[das] Fest der Musik�) ist eine Veranstaltung bei der Amateur- und Berufsmusiker, Performer im Bereich Musik, DJs usw. im �ffentlichen Raum honorarfrei auftreten (zum Beispiel auf �ffentlichen bzw. �ffentlich zug�nglichen Pl�tzen, Fu�wegen/Wegen, in Parks/G�rten/Vorg�rten/H�fen, vor bzw. in Caf�s/Bars/Kneipen/Restaurants, Museen/Galerien, Kirchen, Kiosken/Gesch�ften usw.). Es wird kein Eintrittsgeld verlangt.\n\nDie F�te de la Musique findet jedes Jahr am 21. Juni, dem kalendarischen Sommeranfang, statt � in mehr als 540 St�dten weltweit, davon �ber 300 St�dte in Europa. Deutschlandweit beteiligen sich mittlerweile mehr als 50 St�dte an dem Fest.\n\nQuelle: Wikipedia", Calendar.JUNE, 21),
    GEBET        (0x00008000, "Weltgebetstag",              "Der Weltgebetstag (WGT, auch bekannt unter: Weltgebetstag der Frauen) ist die gr��te �kumenische Basisbewegung von Frauen. Ihr Motto lautet: �Informiert beten � betend handeln�. Der Weltgebetstag wird in �ber 170 L�ndern in �kumenischen Gottesdiensten begangen. Vor Ort bereiten Frauen unterschiedlicher Konfessionen gemeinsam die Gestaltung und Durchf�hrung der Gottesdienste vor. Jedes Jahr schreiben Frauen aus einem anderen Land der Welt die Gottesdienstordnung zum Weltgebetstag. Der Weltgebetstag findet jeweils am ersten Freitag im M�rz statt.\n\nQuelle: Wikipedia"),
    GESUNDHEIT   (0x00010000, "Weltgesundheitstag",         "Der Weltgesundheitstag ist ein weltweiter Aktionstag, mit dem die WHO an ihre Gr�ndung im Jahre 1948 erinnern m�chte. Er wird seit dem Jahr 1954 j�hrlich am 7. April begangen. Jedes Jahr soll am Aktionstag ein vorrangiges Gesundheitsproblem in das Bewusstsein der Welt�ffentlichkeit ger�ckt werden. Dabei geht es zunehmend um Themen, die bei der Entwicklung von nationalen Gesundheitssystemen helfen sollen. In Deutschland wird der fr�here Festtag immer mehr zu einer Informationsplattform f�r Gesundheitsberufe mit Fachtagungen und Kongressen.\n\nQuelle: Wikipedia", Calendar.APRIL, 7),
    BIER         (0x00020000, "Internationaler Tag des Bieres", "Der Internationale Tag des Bieres (engl. International Beer Day) findet j�hrlich am ersten Freitag im August statt. An diesem Tag wird weltweit das Getr�nk Bier gefeiert und getrunken.\nDer Internationale Tag des Bieres verfolgt drei Ziele:\n - Freunde treffen, um gemeinsam Bier zu genie�en.\n - Die M�nner und Frauen zu ehren, welche das Bier brauen und servieren.\n - Gemeinsam die Biere aller Nationen und Kulturen zu feiern und damit die Welt zu vereinen.\n\nQuelle: Wikipedia"),
    JAZZ         (0x00040000, "Internationaler Tag des Jazz", "Internationaler Tag des Jazz ist der 30. April. Die 36. Generalkonferenz der UNESCO hatte ihn im November 2011 ausgerufen. Ziel des Gedenk- und Aktionstages ist, an �die k�nstlerische Bedeutung des Jazz, seine Wurzeln und seine weltweiten Auswirkungen auf die kulturelle Entwicklung erinnern.�\nEr wurde 2012 erstmals begangen; an der Auftaktveranstaltung in Paris am 27. April wirkte auch Klaus Doldinger mit.\n\nQuelle: Wikipedia", Calendar.APRIL, 30),
    KAUF_NIX     (0x00080000, "Kauf-Nix-Tag",               "Der Kauf-Nix-Tag (englisch: �Buy Nothing Day�) ist ein konsumkritischer Aktionstag am letzten Freitag (Nordamerika) bzw. Samstag (Europa) im November. Dieser wird mittlerweile in etwa 45 L�ndern organisiert.\nDurch einen 24-st�ndigen Konsumverzicht soll mit dem Buy Nothing Day gegen �ausbeuterische Produktions- und Handelsstrategien internationaler Konzerne und Finanzgruppen� protestiert werden. Au�erdem soll zum Nachdenken �ber das eigene Konsumverhalten und die weltweiten Auswirkungen angeregt werden. Ein bewusstes, auf Nachhaltigkeit abzielendes Kaufverhalten jedes Einzelnen soll somit gef�rdert werden.\n\nQuelle: Wikipedia"),
    LACH         (0x00100000, "Weltlachtag",                "Der Weltlachtag ist ein Welttag, der j�hrlich am ersten Sonntag im Mai begangen wird. Die Idee stammt aus der Yoga-Lachbewegung, die weltweit in �ber 6.000 Lachclubs in mehr als 100 L�ndern auf allen Kontinenten organisiert ist. Punkt 14:00 Uhr deutscher Zeit (12:00 GMT) wird dabei in Europa gemeinsam f�r eine Minute gelacht.\nDer Weltlachtag wurde 1998 von Madan Kataria, dem Gr�nder der weltweiten Lachyoga-Bewegung, ins Leben gerufen. Die Feier des Weltlachtags soll den Weltfrieden verk�rpern und hat das Ziel, ein globales Bewusstsein der Gesundheit, des Gl�cks und des Friedens durch das Lachen zu erreichen.\n\nQuelle: Wikipedia"),
    LEHRER       (0x00200000, "Weltlehrertag",              "Der Weltlehrertag wird seit 1994 j�hrlich am 5. Oktober begangen, im Gedenken an die �Charta zum Status der Lehrerinnen und Lehrer�, die 1964 von der UNESCO und der Internationalen Arbeitsorganisation angenommen wurde.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 5),
    MAENNER      (0x00400000, "Weltm�nnertag",              "Der Weltm�nnertag ist ein Aktionstag zur M�nnergesundheit, der seit dem Jahr 2000 j�hrlich am 3. November stattfindet. Dieser sollte laut Aussage des Schirmherrn Michail Gorbatschow das Bewusstsein der M�nner im gesundheitlichen Bereich erweitern. So liege die Lebenserwartung der M�nner im Durchschnitt sieben Jahre unter der der Frauen. Neben M�nnergesundheit waren in Deutschland auch Wehrpflicht und Zukunftsperspektiven f�r Jungen Themenschwerpunkte.\n\nQuelle: Wikipedia", Calendar.NOVEMBER, 3),
    MEER         (0x00800000, "Tag des Meeres",             "Der Tag des Meeres hat seinen Ursprung im Erdgipfel am 8. Juni 1992 in Rio de Janeiro. Seit 2009 wird der 8. Juni als Tag des Meeres von den Vereinten Nationen begangen. Die Ozeane werden als bedeutend f�r Ern�hrungssicherheit, Gesundheit und dem �berleben allen Lebens, f�r das Klima und als ein kritischer Teil der Biosph�re gesehen. Ziel des Tages ist es daher, weltweit Aufmerksamkeit f�r aktuelle Herausforderungen im Zusammenhang mit den Ozeanen zu erlangen.\n\nQuelle: Wikipedia", Calendar.JUNE, 8),
    METEOROLOG   (0x01000000, "Welttag der Meteorologie",   "Der Welttag der Meteorologie, Internationaler Tag der Meteorologie bzw. Weltwettertag findet j�hrlich weltweit am 23. M�rz statt und soll an die 1950 in Kraft getretene Konvention der Weltorganisation f�r Meteorologie (WMO), die ihren Sitz in Genf hat, erinnern. Damals begann eine friedliche Zusammenarbeit zwischen den verschiedensten Nationen, die ohne Beispiel war. Deutschland wird in dieser Organisation seit 1954 durch den Deutschen Wetterdienst (DWD) vertreten, �sterreich trat 1957 bei. Man hatte erkannt, dass sich aus weltumspannenden aktuellen Wettermeldungen verl�sslichere Wetterprognosen erstellen lie�en. So konnten selbst Kriege oder andere Ideologien den Datenaustausch nicht verhindern. Selbst Krisengebiete kooperierten ohne R�cksicht auf �politische Gro�wetterlagen�\n\nQuelle: Wikipedia", Calendar.MARCH, 23),
    MUSEUM       (0x02000000, "Internationaler Museumstag", "Der Internationale Museumstag ist ein seit 1978 j�hrlich stattfindendes internationales Ereignis, bei dem am dritten Sonntag im Mai auf die Vielfalt und Bedeutung der Museen aufmerksam gemacht wird. In Deutschland steht der Tag unter der Schirmherrschaft des amtierenden Bundesratspr�sidenten.\nZahlreiche Museen � von den Heimat- und Regionalmuseen bis hin zu den gro�en staatlichen Einrichtungen � pr�sentieren sich an diesem Tag mit besonderen Aktionen wie Sonderf�hrungen, einem Blick hinter die Kulissen, Workshops, Museumsfesten und langen Museumsn�chten bei freiem Eintritt.\n\nQuelle: Wikipedia"),
    MUSIK        (0x04000000, "Weltmusiktag",               "Der Weltmusiktag ist ein Tag, der international der Musik gewidmet ist. Er findet j�hrlich am 1. Oktober statt.\nDer Weltmusiktag wurde 1975 vom Internationalen Musikrat unter der Leitung des damaligen IMC-Pr�sidenten Yehudi Menuhin (USA) ins Leben gerufen, um Musik in allen Bev�lkerungsgruppen zu f�rdern und entsprechend den Idealen der UNESCO (Friede und Freundschaft der V�lker) eine gegenseitige Anerkennung der k�nstlerischen Werte sicherzustellen sowie den internationalen Erfahrungsaustausch im Bereich der Musik zu f�rdern. Anl�sslich des Weltmusiktages finden vielf�ltige Aktionen -insbesondere Konzerte- statt.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 1),
    MAEDCHEN     (0x08000000, "Internationaler M�dchentag", "Der Internationale M�dchentag (auch Welt-M�dchentag genannt, englisch: International Day of the Girl Child) ist ein von den Vereinten Nationen (UNO) initiierter Aktionstag. Er soll in jedem Jahr am 11. Oktober einen Anlass geben, um auf die weltweit vorhandenen Benachteiligungen von M�dchen hinzuweisen.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 11),
    NICHTRAUCH   (0x10000000, "Weltnichtrauchertag",        "Der Weltnichtrauchertag findet allj�hrlich am 31. Mai statt; die Weltgesundheitsorganisation (WHO) hat ihn 1987 ins Leben gerufen.\n�hnlich wie der Weltdrogentag gibt der Weltnichtrauchertag jedes Jahr Anlass f�r Aktionen und Pressemitteilungen der Gesundheitsorganisationen und -initiativen.\n\nQuelle: Wikipedia", Calendar.MAY, 31),
    PHILOSOPHIE  (0x20000000, "Welttag der Philosophie",    "Der Welttag der Philosophie wird weltweit am dritten Donnerstag im November begangen.\nDie UNESCO-Generalkonferenz 2005 erkl�rte in der Resolution 33C/Res. 37, dass der weltweit begangene Aktionstag �der Philosophie zu gr��erer Anerkennung verhelfen und ihr und der philosophischen Lehre Auftrieb verleihen� soll. Alle Mitgliedsl�nder werden in dieser Resolution aufgefordert, den Tag in Schulen, Hochschulen, Institutionen, St�dten oder philosophischen Vereinen aktiv zu gestalten.\n\nQuelle: Wikipedia"),
    RADIO        (0x40000000, "Welttag des Radios",         "Der Welttag des Radios (englisch World Radio Day; kurz: Weltradiotag) wird am 13. Februar begangen. Er fand 2012 zum ersten Mal statt. Die Generalkonferenz der UNESCO hat den Weltradiotag in Erinnerung an die Gr�ndung des United Nations Radio am 13. Februar 1946 aufgerufen.\n\nQuelle: Wikipedia", Calendar.FEBRUARY, 13),
    SPIEL        (0x80000000, "Weltspieltag",               "Der Weltspieltag ist ein internationaler Aktionstag. Er wird jedes Jahr am 28. Mai begangen.\nWunsch der Veranstalter ist es, Kinder und Erwachsene verschiedener sozialer Schichten durch das Spielen einander n�her zu bringen und den Spa� am Spielen zu f�rdern.\n\nQuelle: Wikipedia", Calendar.MAY, 28),
    
    /** World days / memorial days, part 2. */
    AIDS         (0x00000001, "Welt-AIDS-Tag",              "Der Welt-AIDS-Tag wird j�hrlich von der UNAIDS � der AIDS-Organisation der Vereinten Nationen � organisiert und findet am 1. Dezember statt.\nRund um den Globus erinnern am 1. Dezember verschiedenste Organisationen an das Thema AIDS und rufen dazu auf, aktiv zu werden und Solidarit�t mit HIV-Infizierten, AIDS-Kranken und den ihnen nahestehenden Menschen zu zeigen. Der Welt-AIDS-Tag dient auch dazu, Verantwortliche in Politik, Massenmedien, Wirtschaft und Gesellschaft � weltweit wie auch in Europa und Deutschland � daran zu erinnern, dass die HIV-/AIDS-Pandemie weiter besteht.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 1),
    ALZHEIMER    (0x00000002, "Welt-Alzheimertag",          "Welt-Alzheimertag ist der 21. September. Seit 1994 finden am 21. September in aller Welt vielf�ltige Aktivit�ten statt, um die �ffentlichkeit auf die Situation der Alzheimer-Kranken und ihrer Angeh�rigen aufmerksam zu machen. Weltweit sind etwa 35 Millionen Menschen von Demenzerkrankungen betroffen, zwei Drittel davon in Entwicklungsl�ndern. Bis 2050 wird die Zahl auf voraussichtlich 115 Millionen ansteigen, besonders dramatisch in China, Indien und Lateinamerika.\n\nQuelle: Wikipedia", Calendar.SEPTEMBER, 21),
    HAND_WASCH   (0x00000004, "Internationaler H�nde-Waschtag", "Der Internationale H�nde-Waschtag wurde von der Weltgesundheitsorganisation (WHO) ins Leben gerufen und findet j�hrlich am 15. Oktober statt; zum ersten Mal wurde er im Jahr 2008 begangen.\nEin regelm��iges und ordentliches Waschen der H�nde � nach Einseifen Reiben nicht unter einer halben Minute und besonders auch der Fingerkuppen und der Daumen � verhindert die Ausbreitung von Infektionskrankheiten wie beispielsweise des Influenza-A-Virus H1N1, da ein Gro�teil aller ansteckenden Krankheiten �ber die H�nde �bertragen wird.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 15),
    HEPATITS     (0x00000008, "Welt-Hepatitis-Tag",         "Sch�tzungsweise 500 Millionen Menschen weltweit haben entweder Hepatitis B oder Hepatitis C. Dies bedeutet, dass statistisch gesehen weltweit jeder 12. Mensch von Hepatitis betroffen ist.\nDer Welt-Hepatitis-Tag verfolgt das Ziel der globalen Sensibilisierung der Bev�lkerung zu den Themen Hepatitis B und Hepatitis C und zur Ermutigung von Pr�vention, Diagnose und Behandlung. Seit 2011 wird er am 28. Juli, dem Geburtstag von Baruch Samuel Blumberg, begangen.\n\nQuelle: Wikipedia", Calendar.JULY, 28),
    KINDERBUCH   (0x00000010, "Internationaler Kinderbuchtag", "Der Internationale Kinderbuchtag (englisch International Children�s Book Day) ist ein internationaler Aktionstag, der die Freude am Lesen unterst�tzen und das Interesse an Kinder- und Jugendliteratur f�rdern soll. Er wird seit dem Jahr 1967 j�hrlich am 2. April, dem Geburtstag des bekannten Dichters und Schriftstellers Hans Christian Andersen, begangen und wurde durch das International Board on Books for Young People (IBBY) gegr�ndet.\n\nQuelle: Wikipedia", Calendar.APRIL, 2),
    FERNMELDE    (0x00000020, "Weltfernmeldetag",           "Der Weltfernmeldetag (en.: World Information Society Day) wurde 1967 durch die Internationale Fernmeldeunion ausgerufen und ist von der UNO als internationaler Gedenktag anerkannt. Der Tag findet, in Anlehnung an das Gr�ndungsdatum der Internationalen Fernmeldeunion ITU im Jahre 1865, j�hrlich am 17. Mai statt.\n\nQuelle: Wikipedia", Calendar.MAY, 17),
    ANTI_KORRUP  (0x00000040, "Welt-Anti-Korruptionstag",   "Der Welt-Anti-Korruptions-Tag wird seit 2003 am 9. Dezember begangen, als das �bereinkommen der Vereinten Nationen gegen Korruption in M�rida (Mexiko) zur Unterzeichnung vorlag.\nEr ist eine j�hrliche Veranstaltung der UN, also gef�rdert von den Vereinten Nationen mit dem Ziel, das Bewusstsein f�r Korruption und damit zusammenh�ngende Fragen zu verst�rken, und die Menschen, die Korruption in ihren Gemeinden und Regierungen bek�mpfen, aufzuzeigen.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 9),
    KRANKE       (0x00000080, "Welttag der Kranken",        "Der Welttag der Kranken wurde 1993 anl�sslich des Gedenkens an alle von Krankheiten heimgesuchten und gezeichneten Menschen von Papst Johannes Paul II. eingef�hrt. Er wird j�hrlich am 11. Februar, dem Gedenktag Unserer Lieben Frau in Lourdes begangen. Neben einem Gottesdienst im Petersdom finden jeweils zentrale Veranstaltungen in einem anderen Land statt.\n\nQuelle: Wikipedia", Calendar.FEBRUARY, 11),
    KULTUR_ENTW  (0x00000100, "Welttag f�r kulturelle Entwicklung", "Der Welttag der kulturellen Vielfalt f�r Dialog und Entwicklung (kurz auch Welttag f�r kulturelle Entwicklung; englisch World Day for Cultural Diversity, for Dialogue and Development) ist ein Aktionstag der UNESCO, der j�hrlich am 21. Mai begangen wird.\nAnl�sslich der von der 31. Generalversammlung der UNESCO im November 2001 verabschiedeten Allgemeinen Erkl�rung zur kulturellen Vielfalt wurde auch der Welttag f�r kulturelle Entwicklung ausgerufen. Er soll Bewusstsein f�r kulturelle Vielfalt schaffen und den Beitrag von K�nstlern zum Dialog der Kulturen betonen.\n\nQuelle: Wikipedia", Calendar.MAY, 21),
    LEPRA        (0x00000200, "Welt-Lepra-Tag",             "Der Welt-Lepra-Tag ist ein internationaler Gedenk- und Aktionstag, welcher j�hrlich am letzten Sonntag im Januar begangen wird.\nDer Welt-Lepra-Tag wurde 1954 von dem Franzosen Raoul Follereau, �Apostel der Leprakranken� eingef�hrt. Er wollte damit auf die Not der Betroffenen aufmerksam machen. Damals z�hlte die WHO ca. 15 Millionen Leprakranke weltweit, eine Heilung war nicht m�glich.\nHeute ist Lepra im fr�hen Stadium heilbar, aber noch immer nicht ausgerottet. J�hrlich erkranken �ber 200.000 Menschen daran. Die Bedeutung des Welt-Lepra-Tages ist daher immer noch aktuell und inzwischen eine feste Institution in etwa 130 L�ndern der Welt.\n\nQuelle: Wikipedia"),
    NIEREN       (0x00000400, "Weltnierentag",              "Der Weltnierentag wurde 2006 ins Leben gerufen und steht seither jedes Jahr unter einem besonderen Motto, an dem sich die Aktivit�ten der nationalen Organisationen orientieren. Er findet j�hrlich am 2. Donnerstag im M�rz statt.\nDie Ziele des internationalen Weltnierentages sind es die Bedeutung des Organs im menschlichen Organismus aufzuzeigen und das Bewusstsein �ber die enorme Leistung unserer Nieren zu steigern. Die H�ufigkeit und die Auswirkungen von chronischen Nierenerkrankungen und die damit verbundenen gesundheitlichen Probleme sollen weltweit reduziert werden.\n\nQuelle: Wikipedia"),
    POLIO        (0x00000800, "Welt-Poliotag",              "Der Welt-Poliotag (englisch World Polio Day) wird j�hrlich am 28. Oktober begangen.\nDer Aktions- und Gedenktag wurde von der Weltgesundheitsorganisation (WHO) erstmals im Jahr 1988 ins Leben gerufen. Hintergrund f�r den Tag ist die weltweite Kampagne der WHO zur Ausrottung von Polio Global polio eradication initiative (GPEI).\nDas Datum wurde gew�hlt, weil der Entwickler des ersten Impfstoffs gegen Polio (Kinderl�hmung), Jonas Salk, an diesem Tag geboren worden ist.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 28),
    WFMA         (0x00001000, "Welttag f�r menschenw�rdige Arbeit", "Der Welttag f�r menschenw�rdige Arbeit (WFMA, englisch world day for decent work, WDDW) wird j�hrlich am 7. Oktober begangen.\nDer Aktionstag wurde vom Internationalen Gewerkschaftsbund (IGB) bei dessen Neugr�ndung im Jahr 2006 als internationaler Tag f�r Gute Arbeit ins Leben gerufen.\nAn diesem Tag treten die Gewerkschaften weltweit und �ffentlich f�r die Herstellung menschenw�rdiger Arbeitsbedingungen ein und weisen damit auf ein Hauptanliegen des IGB hin.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 7),
    SOZ_KOMM     (0x00002000, "Welttag der sozialen Kommunikationsmittel", "Der Tag der sozialen Kommunikationsmittel wurde 1967 von Papst Paul VI. als Welttag der Massenmedien eingef�hrt. Er findet sechs Wochen nach Ostersonntag bzw. drei Tage nach Christi Himmelfahrt statt. Im Blick auf diesen Tag ver�ffentlicht der jeweilige Papst zum Fest des heiligen Franz von Sales � des Patrons der Journalisten (24. Januar) � eine Botschaft, die die Christliche Soziallehre bez�glich der Ethik der Massenmedien erl�utert.\n\nQuelle: Wikipedia"),
    SPORT        (0x00004000, "Internationaler Tag des Sports", "Der Internationale Tag des Sports f�r Entwicklung und Frieden (englisch international day of sport for development and peace) wird j�hrlich am 6. April begangen.\nDer Gedenktag wurde von der Generalversammlung der Vereinten Nationen am 23. August 2013 in der Resolution A/RES/67/296 einstimmig beschlossen. Er soll die inneren Werte des Sports, wie Fairness, Zusammenarbeit und Respekt f�r den Gegner, f�rdern.\n\nQuelle: Wikipedia", Calendar.APRIL, 6),
    SPRACHEN     (0x00008000, "Europ�ischer Tag der Sprachen", "Der Europ�ische Tag der Sprachen geht auf eine Initiative des Europarates zur�ck. Ziel des Aktionstages ist es, zur Wertsch�tzung aller Sprachen und Kulturen beizutragen, den Menschen die Vorteile von Sprachkenntnissen bewusst zu machen, die individuelle Mehrsprachigkeit zu f�rdern und die Menschen in Europa zum lebensbegleitenden Lernen von Sprachen zu motivieren. Dabei soll das reiche Erbe der 200 europ�ischen Sprachen bewahrt werden.\n\nQuelle: Wikipedia", Calendar.SEPTEMBER, 26),
    TANZ         (0x00010000, "Welttag des Tanzes",         "Der Welttanztag wurde vom Internationalen Komitee des Tanzes des Internationalen Theater Institutes (ITI der UNESCO) angeregt, und im Jahr 1982 erstmals ausgerufen, um den Tanz als universelle Sprache in der Welt zu w�rdigen. Er findet weltweit jedes Jahr am 29. April statt, dem Geburtstag des franz�sischen T�nzers und Choreografen Jean-Georges Noverre (1727�1810), dem Gr�nder des modernen Ballets.\n\nQuelle: Wikipedia", Calendar.APRIL, 29),
    TIERSCHUTZ   (0x00020000, "Welttierschutztag",          "Der Welttierschutztag ist ein internationaler Aktionstag f�r den Tierschutz, der am 4. Oktober begangen wird.\nAn diesem Tag gedenkt man des Heiligen Franz von Assisi (Namenstag), der am Abend des 3. Oktober 1226 gestorben ist und der als Gr�nder des Franziskanerordens unter anderem wegen seiner Tierpredigten ber�hmt und volkst�mlich wurde.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 4),
    TOILETTEN    (0x00040000, "Welttoilettentag",           "Als Welttoilettentag wurde der 19. November erstmals 2001 von der Welttoilettenorganisation ausgerufen. Am 24. Juli 2013 hat die Generalversammlung der Vereinten Nationen einstimmig, auf Vorschlag Singapurs, den 19. November zum Welt-Toiletten-Tag der Vereinten Nationen erkl�rt, im Kampf f�r Sanit�ranlagen. Hintergrund ist das Fehlen ausreichend hygienischer Sanit�reinrichtungen f�r mehr als 40 Prozent der Weltbev�lkerung und dadurch bedingt verschmutztes Wasser sowie wasserb�rtige Krankheiten, was gesundheitliche und sozio-�konomische Folgen nach sich zieht.\n\nQuelle: Wikipedia", Calendar.NOVEMBER, 19),
    TOURISMUS    (0x00080000, "Welttourismustag",           "Den Welttourismustag begeht die Welttourismusorganisation (UNWTO) seit 1980 jedes Jahr am 27. September. Das Datum geht zur�ck auf die Ratifizierung der UNWTO-Statuten im Jahr 1970.\nDer Welttourismustag zeigt die Bedeutung des Tourismus f�r die internationale Gemeinschaft sowie seine Auswirkungen auf soziale, kulturelle, politische und wirtschaftliche Werte weltweit.\n\nQuelle: Wikipedia", Calendar.SEPTEMBER, 27),
    TUBERKULOSE  (0x00100000, "Welt-Tuberkulosetag",        "Der Welttuberkulosetag (Abk.: Welt-TB-Tag) ist ein Gedenktag und f�llt auf den 24. M�rz eines jeden Jahres, um so die Erinnerung an die Tuberkulose in der �ffentlichkeit wachzuhalten, durch die auch heute noch jedes Jahr etwa 940.000 Menschen sterben (die meisten davon in der Dritten Welt).\nDieser Tag im Jahr wurde bewusst gew�hlt, denn am 24. M�rz 1882 gab Robert Koch in Berlin die Entdeckung des Tuberkulose-Bakteriums bekannt. Zu dieser Zeit war die Tuberkulose in Europa und Amerika derart verbreitet, dass jeder siebte daran starb. Durch die Entdeckung der Krankheitsursache war es m�glich, eine Therapie gegen die Krankheit zu entwickeln.\n\nQuelle: Wikipedia", Calendar.MARCH, 24),
    UMWELT       (0x00200000, "Weltumwelttag",              "Der Weltumwelttag oder auch Tag der Umwelt ist ein Aktionstag, der am 5. Juni gefeiert wird.\nAm 5. Juni 1972, dem Er�ffnungstag der ersten Weltumweltkonferenz in Stockholm, wurde der Weltumwelttag offiziell vom United Nations Environment Programme (Umweltprogramm der Vereinten Nationen) ausgerufen. Seitdem beteiligen sich weltweit j�hrlich rund 150 Staaten an diesem World Environment Day (WED). Seit 1976 werden zum Weltumwelttag auch in Deutschland Aktionen zum Recycling, zur Naturzerst�rung weltweit und zur Sch�rfung des Umweltbewusstseins organisiert.\n\nQuelle: Wikipedia", Calendar.JUNE, 5),
    VEGAN        (0x00400000, "Weltvegantag",               "Der Weltvegantag (englisch World Vegan Day) ist ein internationaler Aktionstag, der erstmals am 1. November 1994 anl�sslich des f�nfzigsten Jahrestags der Gr�ndung der Vegan Society stattfand und seitdem j�hrlich am 1. November gefeiert wird.\nIn Deutschland finden am Weltvegantag mehrere Informationsveranstaltungen und Aktionen rund um das vegane Leben statt.\n\nQuelle: Wikipedia", Calendar.NOVEMBER, 1),
    VERBRAUCHER  (0x00800000, "Weltverbrauchertag",         "Der Weltverbrauchertag (englisch: World Consumer Rights Day (WCRD)) ist ein Aktionstag der internationalen Verbraucherorganisation Consumers International, an dem auf Verbraucherbelange �ffentlich aufmerksam gemacht wird. Er wird seit dem Jahr 1983 j�hrlich am 15. M�rz begangen. Der Weltverbrauchertag geht zur�ck auf den US-Pr�sidenten John F. Kennedy, der am 15. M�rz 1962 vor dem amerikanischen Kongress drei grundlegende Verbraucherrechte proklamierte.\n\nQuelle: Wikipedia", Calendar.MARCH, 15),
    WASSER       (0x01000000, "Weltwassertag",              "Der Weltwassertag findet seit 1993 jedes Jahr am 22. M�rz statt. Seit 2003 wird er von UN-Water organisiert.\nNeben den UN-Mitgliedsstaaten haben auch einige Nichtstaatliche Organisationen, die f�r sauberes Wasser und Gew�sserschutz k�mpfen, den Weltwassertag dazu genutzt, die �ffentliche Aufmerksamkeit auf die kritischen Wasserthemen unserer Zeit zu lenken. Eine Milliarde Menschen haben keinen Zugang zu sicherem und sauberem Trinkwasser und vielfach spielt die Geschlechtszugeh�rigkeit eine Rolle beim Wasserzugang.\n\nQuelle: Wikipedia", Calendar.MARCH, 22),
    RHEUMA       (0x02000000, "Welt-Rheumatag",             "Der Welt-Rheuma-Tag (engl.: world arthritis day) wurde erstmals 1996 von der Arthritis and Rheumatism International (ARI) ins Leben gerufen, der internationalen Vereinigung von Selbsthilfeverb�nden Rheumabetroffener. Ziel ist es, die Anliegen rheumakranker Menschen an diesem Tag in das Bewusstsein der �ffentlichkeit zu r�cken. Der Welt-Rheuma-Tag findet immer am 12. Oktober weltweit statt.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 12),
    OSTEOP       (0x04000000, "Welt-Osteoporosetag",        "Der Weltosteoporosetag (englisch world osteoporosis day) wird j�hrlich am 20. Oktober begangen.\nSeit 1999 steht der Tag in jedem Jahr unter einem eigenen Motto, das auf einen Aspekt der Gef�hrdung, an Osteoporose zu erkranken oder eine Erkrankung zu erkennen oder zu vermeiden, hinweist. Zum Weltosteoporosetag veranstalten die Internationale Osteoporose-Stiftung und die nationalen medizinischen Fachgesellschaften sowie die Selbsthilfegruppen und ihre Verb�nde entsprechende Aufkl�rungsveranstaltungen �ber die Krankheit.\n\nQuelle: Wikipedia", Calendar.OCTOBER, 20),
    WIND         (0x08000000, "Global Wind Day",            "Der Global Wind Day (im Deutschsprachigen Raum auch als Tag des Windes bezeichnet) ist ein Aktionstag der Windenergiebranche, der seit 2007 j�hrlich am 15. Juni begangen wird.\nZiel des Aktionstages ist �ffentlichkeitsarbeit. Mittels verschiedener Aktion wie Foto- und Malwettbewerben, Drachensteigen, Besichtigungen von Windparks und ggf. Besteigungen von Windkraftanlagen usw. soll auf die Bedeutung der Windenergienutzung aufmerksam gemacht werden.\n\nQuelle: Wikipedia", Calendar.JUNE, 15),
    WISSENSCH    (0x10000000, "Welttag der Wissenschaft",   "Der Welttag der Wissenschaft f�r Frieden und Entwicklung (englisch: World Science Day for Peace and Development, WSDPD) ist ein von der Organisation der Vereinten Nationen f�r Erziehung, Wissenschaft und Kultur (UNESCO) ausgerufener weltweiter Gedenk- und Aktionstag, der an den bedeutenden Beitrag der Wissenschaften f�r Frieden und Entwicklung erinnern soll. Au�erdem soll er die Beitr�ge der UNESCO zur Wissenschaft w�rdigen. Im Rahmen des Welttages der Wissenschaft findet die Verleihung der UNESCO-Wissenschaftspreise statt.\n\nQuelle: Wikipedia", Calendar.NOVEMBER, 10),
    FEUCHT       (0x20000000, "Welttag der Feuchgebiete",   "Der Welttag der Feuchtgebiete wird seit 1997 j�hrlich am 2. Februar begangen, im Gedenken an die Ramsar-Vereinbarung (�bereinkommen �ber Feuchtgebiete, insbesondere als Lebensraum f�r Wasser- und Watv�gel, von internationaler Bedeutung), die von der UNESCO angesto�en wurde. Der Tag soll die �ffentliche Wahrnehmung des Wertes und der Vorz�ge von Feuchtgebieten verbessern.\n\nQuelle: Wikipedia", Calendar.FEBRUARY, 2),
    MENSCHEN_RE  (0x40000000, "Tag der Menschenrechte",     "Der Tag der Menschenrechte wird am 10. Dezember gefeiert und ist der Gedenktag zur Allgemeinen Erkl�rung der Menschenrechte, die am 10. Dezember 1948 durch die Generalversammlung der Vereinten Nationen verabschiedet wurde.\nMenschenrechtsorganisationen wie Amnesty International nehmen diesen Tag jedes Jahr zum Anlass, die Menschenrechtssituation weltweit kritisch zu betrachten und auf aktuelle Brennpunkte hinzuweisen.\nDas Europ�ische Parlament verleiht um diesen Tag j�hrlich den Sacharow-Preis, die Organisation Reporter ohne Grenzen ihren Menschenrechtspreis.\nJedes Jahr am 10. Dezember, dem Todestag Alfred Nobels, wird in Oslo (Norwegen) der Friedensnobelpreis verliehen.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 10),
    KREBS        (0x80000000, "Weltkrebstag",               "Der Weltkrebstag findet j�hrlich am 4. Februar statt und hat zum Ziel, die Vorbeugung, Erforschung und Behandlung von Krebserkrankungen ins �ffentliche Bewusstsein zu r�cken. Er wurde 2006 von der Union internationale contre le cancer, der Weltgesundheitsorganisation und anderen Organisationen ins Leben gerufen.\n\nQuelle: Wikipedia", Calendar.FEBRUARY, 4),
    
    /** Time shift. */
    ZeitumstellungVor(0, "Zeitumstellung (Sommerzeit)", "von 2 Uhr vor auf 3 Uhr"),
    ZeitumstellungNach(0, "Zeitumstellung (Winterzeit)", "von 3 Uhr zur�ck auf 2 Uhr"),
    
    /** Seasons. */
    SPRING(0, "Fr�hlingsanfang", "", Calendar.MARCH, 21),
    SUMMER(0, "Sommeranfang", "Astronomisch beginnt der Sommer mit der Sommersonnenwende � dem Zeitpunkt, zu dem die Sonne senkrecht �ber dem Wendekreis der eigenen Erdh�lfte steht und die Tage am l�ngsten sind. Zur Sommersonnenwende � auf der Nordhalbkugel der Erde am 20., 21. oder 22. Juni � erreicht die Sonne ihren mitt�glichen H�chststand �ber dem Horizont. Auf der S�dhalbkugel sind die Verh�ltnisse umgekehrt. W�hrend des dortigen Winters ist auf der Nordhalbkugel Sommer.\nDie Sommersonnenwende wird in vielen L�ndern, wie in Mitteleuropa und den USA, als Beginn der Jahreszeit Sommer gesehen. Ab da werden die Tage wieder k�rzer.\n\nQuelle: Wikipedia", Calendar.JUNE, 21),
    AUTUMN(0, "Herbstanfang", "", Calendar.SEPTEMBER, 23),
    WINTER(0, "Winteranfang", "Astronomisch beginnt der Winter mit der Wintersonnenwende � dem Zeitpunkt, zu dem die Sonne senkrecht �ber dem Wendekreis der anderen Erdh�lfte steht und die Tage am k�rzesten sind. Zur Wintersonnenwende � auf der Nordhalbkugel der Erde am 21. oder 22. Dezember � erreicht die Sonne die geringste Mittagsh�he �ber dem Horizont. Auf der S�dhalbkugel sind die Verh�ltnisse umgekehrt. W�hrend des dortigen Winters ist auf der Nordhalbkugel Sommer.\nDa ab 21./22. Dezember die Tage wieder l�nger werden, war die Wintersonnenwende in vielen antiken und fr�hmittelalterlichen Kulturen ein wichtiges Fest, das oft ein paar Tage vor bzw. nach dem Datum der tats�chlichen Sonnenwende gefeiert wurde.\n\nQuelle: Wikipedia", Calendar.DECEMBER, 21);
    
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

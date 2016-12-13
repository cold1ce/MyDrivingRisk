package fim.de.mydrivingrisk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

public class Impressum extends AppCompatActivity {

    public TextView t1;
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressum);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle("Impressum");
        t1 = (TextView) findViewById(R.id.textView20);
        t1.setText(Html.fromHtml("<html><p><strong><span style=\"font-size: x-large;\">myDrivingRisk</span><br /></strong>Angaben gem&auml;&szlig; &sect; 5 TMG</p>\n" +
                "<p>Autoren: Julian Ruppert, Benedikt Neubert, Stefan Rasche, Tobias Wittmeir<br />Betreuer: Sebastian Heger, Marco Schmidt</p>\n" +
                "<p>FIM Kernkompetenzzentrum<br />Universit&auml;t Augsburg<br />86135 Augsburg</p>\n" +
                "<p><a href=\"http://www.fim-rc.de/\">www.fim-rc.de<br /></a><a href=\"mailto:mobile@fim-rc.de\">mobile@fim-rc.de</a></p>\n" +
                "<p>F&uuml;r einen Einblick in den Quellcode kontaktieren Sie uns gerne.</p>\n" +
                "<p>Die Anwendung wurde im Rahmen des &bdquo;Projektstudium Wirtschaftsinformatik&ldquo; am Kernkompetenzzentrum Finanz- und Informationsmanagement der Universit&auml;t Augsburg entwickelt. <br />Das Kernkompetenzzentrum Finanz- und Informationsmanagement kooperiert eng mit der Projektgruppe Wirtschaftsinformatik des Fraunhofer Instituts f&uuml;r Angewandte Informationstechnik FIT. <br />Als Vorlage diente die Bachelorarbeit &bdquo;Utilization of driving-related data for customized insurance premiums&ldquo;, welche von Moritz W&ouml;hl vorgelegt wurde.</p>\n" +
                "<p><br /><br /><span style=\"font-size: large;\"><strong>Haftungsausschluss:&nbsp;</strong></span><br /><br /><strong>Haftung f&uuml;r Inhalte</strong><br /><br /><span>Die Inhalte der App wurden mit gr&ouml;&szlig;ter Sorgfalt erstellt. F&uuml;r die Richtigkeit, Vollst&auml;ndigkeit und Aktualit&auml;t der Inhalte k&ouml;nnen wir jedoch keine Gew&auml;hr &uuml;bernehmen. Als Diensteanbieter sind wir gem&auml;&szlig; &sect; 7 Abs.1 TMG f&uuml;r eigene Inhalte in dieser App nach den allgemeinen Gesetzen verantwortlich. Nach &sect;&sect; 8 bis 10 TMG sind wir als Diensteanbieter jedoch nicht verpflichtet, &uuml;bermittelte oder gespeicherte fremde Informationen zu &uuml;berwachen oder nach Umst&auml;nden zu forschen, die auf eine rechtswidrige T&auml;tigkeit hinweisen. Verpflichtungen zur Entfernung oder Sperrung der Nutzung von Informationen nach den allgemeinen Gesetzen bleiben hiervon unber&uuml;hrt. Eine diesbez&uuml;gliche Haftung ist jedoch erst ab dem Zeitpunkt der Kenntnis einer konkreten Rechtsverletzung m&ouml;glich. Bei Bekanntwerden von entsprechenden Rechtsverletzungen werden wir diese Inhalte umgehend entfernen.</span><br /><br /><strong>Haftung f&uuml;r Links</strong><br /><br /><span>Unser Angebot enth&auml;lt Links zu externen Angeboten Dritter, auf deren Inhalte wir keinen Einfluss haben. Deshalb k&ouml;nnen wir f&uuml;r diese fremden Inhalte auch keine Gew&auml;hr &uuml;bernehmen. F&uuml;r die Inhalte der verlinkten Seiten ist stets der jeweilige Anbieter oder Betreiber der Seiten verantwortlich. Die verlinkten Drittangebote wurden zum Zeitpunkt der Verlinkung auf m&ouml;gliche Rechtsverst&ouml;&szlig;e &uuml;berpr&uuml;ft. Rechtswidrige Inhalte waren zum Zeitpunkt der Verlinkung nicht erkennbar. Eine permanente inhaltliche Kontrolle der verlinkten Inhalte ist jedoch ohne konkrete Anhaltspunkte einer Rechtsverletzung nicht zumutbar. Bei Bekanntwerden von Rechtsverletzungen werden wir derartige Links umgehend entfernen.</span><br /><br /><strong>Urheberrecht</strong><br /><br /><span>Die durch die Betreiber erstellten Inhalte und Werke unterliegen dem deutschen Urheberrecht. Die Vervielf&auml;ltigung, Bearbeitung, Verbreitung und jede Art der Verwertung au&szlig;erhalb der Grenzen des Urheberrechtes bed&uuml;rfen der schriftlichen Zustimmung des jeweiligen Autors bzw. Erstellers. Soweit die Inhalte nicht vom Betreiber erstellt wurden, werden die Urheberrechte Dritter beachtet. Insbesondere werden Inhalte Dritter als solche gekennzeichnet. Sollten Sie trotzdem auf eine Urheberrechtsverletzung aufmerksam werden, bitten wir um einen entsprechenden Hinweis. Bei Bekanntwerden von Rechtsverletzungen werden wir derartige Inhalte umgehend entfernen.</span><br /><br /><strong>Datenschutz</strong><br /><br /><span>Die Nutzung der App ist ohne Angabe personenbezogener Daten m&ouml;glich.&nbsp;</span><br /><span>Wir weisen darauf hin, dass die Daten&uuml;bertragung im Internet Sicherheitsl&uuml;cken aufweisen kann. Ein l&uuml;ckenloser Schutz der Daten vor dem Zugriff durch Dritte ist nicht m&ouml;glich.&nbsp;<br /></span><br /><span>Der Nutzung von im Rahmen der Impressumspflicht ver&ouml;ffentlichten Kontaktdaten durch Dritte zur &Uuml;bersendung von nicht ausdr&uuml;cklich angeforderter Werbung und Informationsmaterialien wird hiermit ausdr&uuml;cklich widersprochen.</span></p>\n" +
                "<p>Die Autoren behalten es sich ausdr&uuml;cklich vor, Teile der Applikation oder das gesamte Angebot ohne gesonderte Ank&uuml;ndigung zu ver&auml;ndern, zu erg&auml;nzen, zu l&ouml;schen oder die Ver&ouml;ffentlichung zeitweise oder endg&uuml;ltig einzustellen.</p>\n" +
                "<p>&nbsp;</p>\n" +
                "<p>Genutzte Application Programming Interfaces:</p>\n" +
                "<p><a href=\"http://www.openstreetmap.org/\">www.openstreetmap.org</a></p>\n" +
                "<p><a href=\"http://www.openweathermap.org/\">www.openweathermap.org</a></p><html>"));
    }
}

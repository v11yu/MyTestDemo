package html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class jsoupTest {
	public void getWid(){
		Document doc = Jsoup.parse("");
		Elements inputs = doc.getElementsByTag("input");
		Element form = doc.getElementsByTag("form").get(0);
	}
}

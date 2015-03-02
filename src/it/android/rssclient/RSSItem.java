package it.android.rssclient;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

 
public class RSSItem {
     
    private String title;
    private String description;
    private Date date;
    private String link;
     
    public RSSItem(String title, String description, Date pubDate, String link) {
        this.title          = title;
        this.description = description;
        this.date           = pubDate;
        this.link         = link;
    }
     
    public String getTitle() {
        return title;
    }
     
    public void setTitle(String title) {
        this.title = title;
    }
     
    public String getDescription() {
        return description;
    }
     
    public void setDescription(String description) {
        this.description = description;
    }
     
    public Date getPubDate() {
        return date;
    }
     
    public void setPubDate(Date pubDate) {
        this.date = pubDate;
    }
     
    public String getLink() {
        return link;
    }
     
    public void setLink(String link) {
        this.link = link;
    }
     
   
   public String toString() {
         
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm - MM/dd/yy");
         
        String result = getTitle() + "   ( " + sdf.format(this.getPubDate()) + " )";
        return result;
    }
    
    public static ArrayList<RSSItem> getRSSItems(String feedUrl) {
         
    	 ArrayList<RSSItem> rssItems = new ArrayList<RSSItem>();
    	                	 
        	 Loading(rssItems,feedUrl);
   
         
        return rssItems;
    }




private static void Loading(ArrayList<RSSItem> rssItems,String feed){
	
    try {
        
        URL url = new URL(feed);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream();
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
             
             
            Document document = db.parse(is);
            Element element = document.getDocumentElement();
             
             
            NodeList nodeList = element.getElementsByTagName("item");
             
            if (nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                     
                    Element entry = (Element) nodeList.item(i);
                    Element _titleE       = (Element)entry.getElementsByTagName("title").item(0);
                    Element _descriptionE = (Element)entry.getElementsByTagName("description").item(0);
                    Element _pubDateE       = (Element) entry.getElementsByTagName("pubDate").item(0);
                    Element _linkE           = (Element) entry.getElementsByTagName("link").item(0);
                    String _title           = _titleE.getFirstChild().getNodeValue();
                    String _description   = _descriptionE.getFirstChild().getNodeValue();
                    Date _pubDate           = new Date(_pubDateE.getFirstChild().getNodeValue());
                    String _link           = _linkE.getFirstChild().getNodeValue();
                     
                    RSSItem rssItem = new RSSItem(_title, _description, _pubDate, _link);
                     
                    rssItems.add(rssItem);
                    
                     
                }
            }
             
        }
          
        } catch (Exception e) {
         
        e.printStackTrace();
         
    }
	}
}













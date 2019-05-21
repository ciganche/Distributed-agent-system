package crawlerUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import webSocket.LoggerUtil;

public class CrawlerUtil 
{
	
	
	
	private Queue<String> kju = new LinkedList<>();
	private ArrayList<Page> crawledPages = new ArrayList<Page>();
	private ArrayList<String> marked = new ArrayList<>();
	private String location;
	
	public CrawlerUtil(String loc)
	{
		location = loc;
	}
	
	public void crawl(String seedUrl, int pageNum)
	{
		String regex = "/wiki/(.+?)\"";
		
		
		marked.add(seedUrl);
		kju.add(seedUrl);
		
		
		while(!kju.isEmpty())
		{
			String crawledURL = kju.poll();
			String info[]= processPage(crawledURL);
			
			
			String name = info[0];
			String descpription = info[1];
			crawledPages.add(new Page(name,descpription,crawledURL));
			
			String html = null;
			html = info[2];
			
			if(html == null || name == null || descpription == null)
			{
				continue;
			}
			
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(html);

			//go trough all links
			while(matcher.find())
			{
				String w = matcher.group();
				String adresa = "https://sh.wikipedia.org" + w.substring(0, w.length()-1); //secenje " karaktera zbog nacina definisanja regex-a

				//empty wiki page
				if(adresa.startsWith("https://sh.wikipedia.org/w/index.php?title") || adresa.startsWith("https://sh.wikipedia.org/wiki/en:"))
				{
					marked.add(adresa);
					continue;
				}
				
				
				//if not visited earlier
				if(!marked.contains(adresa))
				{
					kju.add(adresa);
					marked.add(adresa);
				}
			}
			
			if(crawledPages.size() >= pageNum)
			{
				LoggerUtil.log("Crawler agent collected " + pageNum + " pages.");
				return;
			}		
		}
		LoggerUtil.log("Crawler crawled unable the specified number of pages. Collected only " + crawledPages.size() + ".");
	}

	private String[] processPage(String source)
	{
		String retval[] = {null,null,null};
		try
		{
			Document doc = Jsoup.connect(source).timeout(5*60*1000).get();
			Elements cont_temp = doc.select("div.mw-content-ltr");
			Elements head_temp = doc.select("h1.firstHeading");
			
			String naziv = null;
			String opis = null;

			
			for(Element pom : head_temp)
			{
				naziv = pom.text();			
			}
			retval[0] = naziv;
			
			

			for(Element  paragraf:cont_temp )
			{
				
				if(paragraf.getElementsByTag("p").text().length() == 0)
				{
					Elements pom = paragraf.getElementsByTag("div");
					
					for(Element nesto:pom)
					{						
						if(nesto.getElementsByTag("p").text().length()==0)
						{
							return retval;
						}		
						retval[1] = nesto.getElementsByTag("p").get(0).text();
							
						retval[2] = nesto.getElementsByTag("p").html();
					}
				}
					
				
				else
				{
					opis = paragraf.getElementsByTag("p").get(0).text();
					retval[1] = opis;
					retval[2] =  paragraf.getElementsByTag("p").html();
				}
				
			}

		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println("Unable to parse page: " + source);
		}
		
		return retval;
	}
	
	
	public void saveToFile()
	{
		try
		{
			 OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(location + "/wiki.tsv"), StandardCharsets.UTF_8);
			 
			 for(Page page : crawledPages)
			 {
				writer.write(page.toString());
				writer.write("\n");
			 }
			 
			 writer.close();
			 
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LoggerUtil.log("Crawling error - cannot create a .tsv file.");
		}
	}
}

#  Parse and summarize the RSS feed from 'Planet Python'
# ('http://planet.python.org/rss20.xml') using the
# `xml.etree.ElementTree` class. Fields of interest in the summary: 
# 'title', 'pubDate', 'link'

from urllib.request import urlopen
from xml.etree.ElementTree import parse

rssfeed = urlopen('http://planet.python.org/rss20.xml')
doc = parse(rssfeed)

# Extract relevant info: 
for item in doc.iterfind('channel/item'):
    title = item.findtext('title').encode('utf-8')
    pubdate = item.findtext('pubDate')
    link = item.findtext('link')

    print(title.decode('cp850', errors='ignore'), pubdate, link)


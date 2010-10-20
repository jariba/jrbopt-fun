import feedparser
import re

# returns title and dictionary of word counts for an RSS feed
def getWordCounts(url):
    # parse the feed
    d=feedparser.parse(url)
    wc={}
    
    # Loop over all the entries
    for e in d.entries:
        if 'summary' in e: summary=e.summary
        else: summary=e.description
        
        #Extract a list of words
        words=getwords(e.title+' '+summary)
        for word in words:
            wc.setdefault(word,0)
            wc[word]+=1
            
    return d.feed.title,wc

def getwords(html):
    # remove all the html tags
    txt = re.compile(r'<[^>]+>').sub('',html)
    
    # Split words by all non-alpha characters
    words = re.compile(r'[^A-Z^a-z]+').split(txt)
    
    # convert to lower case
    return [word.lower() for word in words if word!='']


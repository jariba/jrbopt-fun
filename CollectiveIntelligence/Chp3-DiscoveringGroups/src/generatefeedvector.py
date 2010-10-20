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
        words=getWords(e.title+' '+summary)
        for word in words:
            wc.setdefault(word,0)
            wc[word]+=1
            
    return d.feed.title,wc

def getWords(html):
    # remove all the html tags
    txt = re.compile(r'<[^>]+>').sub('',html)
    
    # Split words by all non-alpha characters
    words = re.compile(r'[^A-Z^a-z]+').split(txt)
    
    # convert to lower case
    return [word.lower() for word in words if word!='']

def generateFeedVector(inputFile,outputFile):
    apcount={}
    wordcounts={}
    feedlist=[]
    
    for feedurl in file(inputFile):
        title,wc=getWordCounts(feedurl)
        feedlist.append(title)
        wordcounts[title]=wc
        for word,count in wc.items:
            apcount.setdefault(word,0)
            if count>1:
                apcount[word]+=1
        
        # Only include words that are not too common or too rare        
        wordlist=[]
        for word,blogCount in apcount.items():
            frac=float(blogCount)/len(feedlist)
            if (frac>0.1 and frac<0.5): wordlist.append(word)
            
        out=file(outputFile,'w')
        out.write('Blog')
        for word in wordlist: out.write('\t%s' % word)
        out.write('\n')
        for blog,wc in wordcounts.items():
            out.write(blog)
            for word in wordlist:
                if word in wc: out.write('\t%d' % wc[word])
                else: out.write('\t0')
            out.write('\n')
            
            
generateFeedVector('data/feedlist.txt','blogdata.txt')            
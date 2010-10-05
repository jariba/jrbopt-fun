from pydelicious import get_popular,get_userposts,get_urlposts
import time

def initializeUserDict(tag,count=5):
    user_dict={}
    
    # get the top count popular posts
    for p1 in get_popular(tag=tag)[0:count]:
        # find all users who posted this
        for p2 in get_urlposts(p1['url']):
            user=p2['user']
            user_dict[user]={}
            
    return user_dict


def fillItems(user_dict):
    all_items={}
    #Find links posted by all users
    for user in user_dict:
        for i in range(3):
            try:
                posts=get_userposts(user)
                break
            except:
                print "Failed user "+user+", retrying ("+i+")"
                time.sleep(4)
        
        for post in posts:
            url=post['url']
            user_dict[user][url]=1.0
            all_items[url]=1
            
    # Fill in mission items with 0
    for ratings in user_dict.values():
        for item in all_items:
            if item not in ratings:
                ratings[item]=0.0
                
# To show recs :
# delusers=initializeUserDict('programming')
# fillItems(delusers)

# user-based recommendations:
# import random
# user=delusers.keys()[random.randint(0,len(delusers)-1)]
# recommendations.topMatches(delusers,user)
# recommendations.getRecommendations(delusers,user)[0:10]
#
# item-based recommendations
# url=recommendations.getRecommendations(delusers,user)[0][1]
# recommendations.topMatches(recommendations.transformPrefs(delusers),url)

         
            
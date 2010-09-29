from math import sqrt,pow

# Returns a distance based similarity score for person1 and person2
# Euclidean distance score
def sim_distance(prefs,person1,person2):
    # Get the list of shared_items
    si = {}
    for item in prefs[person1]:
        if item in prefs[person2]:
            si[item]=1
    
    # If they have no ratings in common return 0
    if (len(si)==0): return 0
    
    # Add up the squares of all the differences
    sum_of_squares=sum([pow(prefs[person1][item]-prefs[person2][item],2)
                        for item in prefs[person1] if item in prefs[person2]])
    
    # Normalize so that return value is in [0,1]
    return 1/(1+sqrt(sum_of_squares))

def sim_pearson(prefs,person1,person2):
    si = {}
    for item in prefs[person1]:
        if item in prefs[person2]:
            si[item]=1
    
    # If they have no ratings in common return 0
    if (len(si)==0): return 0
    
    # Add up all the preferences by person
    sum1=sum([prefs[person1][it] for it in si])
    sum2=sum([prefs[person2][it] for it in si])
    
    # Sum up the squares
    sum1Sq=sum([pow(prefs[person1][it],2) for it in si])
    sum2Sq=sum([pow(prefs[person2][it],2) for it in si])
    
    # Sum up the products
    pSum=sum([prefs[person1][it]*prefs[person2][it] for it in si])
    
    # Calculate Pearson score
    n = len(si)
    num = pSum - (sum1*sum2/n)
    den = sqrt((sum1Sq-pow(sum1,2)/n)*(sum2Sq-pow(sum2,2)/n))
    
    if den==0: return  0
    
    r = num/den
    return r


# Returns the best matches for person from the prefs dictionary.
# number of results and similarity function are optional params.
def topMatches(prefs,person,n=5,similarity=sim_pearson):
    scores = [(similarity(prefs,person,other),other) 
              for other in prefs if other != person]
    
    #Sort the list so that top scores appear at the top
    scores.sort()
    scores.reverse()
    return scores[0:n]
    
    
# Get recommendations for a person by using a weighted average of every other user's rankings
def getRecommendations(prefs,person,similarity=sim_pearson): 
    totals={}
    simSums={}
    
    for other in prefs:
        #don't compare to myself
        if (other==person): continue
        
        sim = similarity(prefs,person,other)
        
        #ignore scores of 0 or lower
        if (sim<=0): continue
        
        for item in prefs[other]:
            #only score movies I haven't seen yet
            if (item not in prefs[person] or prefs[person][item]==0):
                # Similarity * Score
                totals.setdefault(item,0)
                totals[item]+=prefs[other][item]*sim
                # Sum of similarities
                simSums.setdefault(item,0)
                simSums[item]+=sim
                
    # Create the normalized list
    rankings=[(total/simSums[item],item) for item,total in totals.items()]
    # return  the sorted list
        
    rankings.sort()
    rankings.reverse()
    return rankings

# Transposes the preference matrix
# in the example below it can be used to move from user-based recs to item-based recs    
def transformPrefs(prefs):
    result = {}
    for person in prefs:
        for item in prefs[person]:
            result.setdefault(item,{})   
            # Flip item and person
            result[item][person]=prefs[person][item]
        
    return result
 
           
    
# A dictionary of movie critics and their ratings on a small set of movies
critics={
         'Lisa Rose':        {'Lady in the Water':2.5, 'Snakes on a Plane':3.5, 'Just My Luck':3.0, 'Superman Returns':3.5, 'You, Me and Dupree':2.5, 'The Night Listener':3.0},
         'Gene Seymour':     {'Lady in the Water':3.0, 'Snakes on a Plane':3.5, 'Just My Luck':1.5, 'Superman Returns':5.0, 'You, Me and Dupree':3.5, 'The Night Listener':3.0},
         'Michael Phillips': {'Lady in the Water':2.5, 'Snakes on a Plane':3.0,                     'Superman Returns':3.5,                           'The Night Listener':4.0},
         'Claudia Puig':     {                         'Snakes on a Plane':3.5, 'Just My Luck':3.0, 'Superman Returns':4.0, 'You, Me and Dupree':2.5, 'The Night Listener':4.5},
         'Mick LaSalle':     {'Lady in the Water':3.0, 'Snakes on a Plane':4.0, 'Just My Luck':2.0, 'Superman Returns':3.0, 'You, Me and Dupree':2.0, 'The Night Listener':3.0},
         'Jack Matthews':    {'Lady in the Water':3.0, 'Snakes on a Plane':4.0,                     'Superman Returns':5.0, 'You, Me and Dupree':3.5, 'The Night Listener':3.0},
         'Toby':             {                         'Snakes on a Plane':4.5,                     'Superman Returns':4.0, 'You, Me and Dupree':1.0}
}

movies = transformPrefs(critics)

#print sim_pearson(critics,'Lisa Rose','Gene Seymour')

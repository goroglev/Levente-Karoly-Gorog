import string
import re
import cgi
import htmllib

def escape(s):
  return cgi.escape(s)

_htmlParser=htmllib.HTMLParser(None)

def unescape(s):
  """http://wiki.python.org/moin/EscapingHtml
     Example:
      >>> unescape("Norwegian&lt;&gt;Polish Dictionar TR&#39;App")
      >>> Norwegian<>Polish Dictionar TR'App
  """
  _htmlParser = htmllib.HTMLParser(None)
  _htmlParser.save_bgn()
  _htmlParser.feed(s)
  return _htmlParser.save_end()

def excelCsvLine(values):
  """
     Excel Csv style line including newLine
  """
  def escapeQuote(v):
    return unicode(v).replace('"','""')
  valuesAsText = [ (u'"%s"' % escapeQuote(v)) for v in values ]
  return (u",".join(valuesAsText))+u"\n"


def dateStrConvert(s):
  """ 'January 07, 2009' to 01/07/2009 """
  fields=s.strip().replace(",","").split()
  monthStr = fields[0]
  day = int(fields[1])
  year = int(fields[2])
  months = ["January","February","March","April","May","June",
             "July","August","September","October","November","December"]
  month = months.index(monthStr)+1
  return "%02d/%02d/%4d" % (month,day,year)

def punctuationToSpace(s):
  for c in string.punctuation:
      s= s.replace(c," ")
  return s

_multiSpaceToSingleRe = re.compile(r'\s+', flags=re.UNICODE)

def multiSpaceToSingle(s):
  return _multiSpaceToSingleRe.sub(' ',s)

def textInBetween(textStr,beforeTag,afterTag,allowEmptyAsNone=False):
  start=findPast(textStr,beforeTag)
  assert start >= 0
  end=textStr.find(afterTag,start)
  assert end >= 0
  result = textStr[start:end]
  if allowEmptyAsNone:
    if not len(result):
      return None
  else:
    assert len(result)
  return result

def listOfTextInBetween(textStr,beforeTag,afterTag):
  resultList = []
  end=0
  while 1:
    start=findPast(textStr,beforeTag,end)
    if start == -1:
      return resultList
    end=textStr.find(afterTag,start)
    assert end >= 0
    result = textStr[start:end]
    assert len(result)
    resultList.append(result)

def textInBetweenAfterSet(textStr,beforeTag,afterTagSet):
  start=findPast(textStr,beforeTag)
  assert start >= 0

  end=len(textStr)
  for afterTag in afterTagSet:
    thisEnd=textStr.find(afterTag,start)
    if thisEnd != -1:
      end = min(end,thisEnd)
  assert end != len(textStr)
  assert end >= 0
  result = textStr[start:end]
  assert len(result)
  return result
 
def commaTextToFloat(textStr):
  return float(textStr.replace(",","."))
def commaTextToInt(textStr):
  return int(textStr.replace(",",""))


def findPast(s, substr, start=0):
  end = s.find(substr, start)
  if end == -1:
    return end
  return end+len(substr)

def toAscii(str):
    return ''.join([c for c in str if (ord(c) < 128) and (ord(c) > 31 or ord(c) == 9)])


def longestCommonSubstring(S1, S2):
  M = [[0]*(1+len(S2)) for i in xrange(1+len(S1))]
  longest, x_longest = 0, 0
  for x in xrange(1,1+len(S1)):
      for y in xrange(1,1+len(S2)):
          if S1[x-1] == S2[y-1]:
              M[x][y] = M[x-1][y-1] + 1
              if M[x][y]>longest:
                  longest = M[x][y]
                  x_longest  = x
          else:
              M[x][y] = 0
  return S1[x_longest-longest: x_longest]


## {{{ http://code.activestate.com/recipes/576874/ (r1)
## Upper bound value is at most the length of the longer string.
''' Calculates the Levenshtein distance of 2 strings'''
## TODO use instead C#  of http://en.wikipedia.org/wiki/Damerau%E2%80%93Levenshtein_distance 
def editingDistance(s1, s2):
  l1 = len(s1)
  l2 = len(s2)

  matrix = [range(l1 + 1)] * (l2 + 1)
  for zz in range(l2 + 1):
    matrix[zz] = range(zz,zz + l1 + 1)
  for zz in range(0,l2):
    for sz in range(0,l1):
      if s1[sz] == s2[zz]:
        matrix[zz+1][sz+1] = min(matrix[zz+1][sz] + 1, matrix[zz][sz+1] + 1, matrix[zz][sz])
      else:
        matrix[zz+1][sz+1] = min(matrix[zz+1][sz] + 1, matrix[zz][sz+1] + 1, matrix[zz][sz] + 1)
  return matrix[l2][l1]

_reRankWordSplitter = re.compile("\s|\.|-|/|\\|,|:|;|_")

# deprecated
def rankOnSubstrMatches(subStrs, stringOrg):
  stringOrgLen = len(stringOrg)
  rank=0
  while True:
    lcs = longestCommonSubstring(subStrs,stringOrg)
    if len(lcs) >= 3:
      rank+=len(lcs)
      subStrs = subStrs.replace(lcs,'')
      stringOrg = stringOrg.replace(lcs,'')
    else:
      return rank / float(stringOrgLen)

# rank how s fits in text
#   the smaller the number the better it fits, including negative numbers
# If no chars are matching (upper bound editDistance) then 0 is returned
#  best used with all lower cased().
#  and normalized spacing
def rankMe(s, text):
  # complete match, best
  if s == text:
    return -100000
  v = text.find(s)
  # sub string on word boundary, next best
  if v != -1 and(v==0 or text[v-1]==' '):
    # penalize for extra chars
    return -100000+len(text)-len(s)
  textWords = set(_reRankWordSplitter.split(text))
  sWords = _reRankWordSplitter.split(s)
  nrWordsinText = 0
  for w in sWords:
    nrWordsinText += w in textWords
  if nrWordsinText:
    # rank for number of words matched
    # penalize for larger text body
    return (128*-nrWordsinText)+len(text)
  v = editingDistance(s, text)
  if v == max(len(s),len(text)):
     return 0
  return v
 
def commaTextToFloat(textStr):
  return float(textStr.replace(",","."))
def commaTextToInt(textStr):
  return int(textStr.replace(",",""))


def findPast(s, substr, start=0):
  end = s.find(substr, start)
  if end == -1:
    return end
  return end+len(substr)

def toAscii(str):
    return ''.join([c for c in str if (ord(c) < 128) and (ord(c) > 31 or ord(c) == 9)])


def longestCommonSubstring(S1, S2):
  M = [[0]*(1+len(S2)) for i in xrange(1+len(S1))]
  longest, x_longest = 0, 0
  for x in xrange(1,1+len(S1)):
      for y in xrange(1,1+len(S2)):
          if S1[x-1] == S2[y-1]:
              M[x][y] = M[x-1][y-1] + 1
              if M[x][y]>longest:
                  longest = M[x][y]
                  x_longest  = x
          else:
              M[x][y] = 0
  return S1[x_longest-longest: x_longest]


## {{{ http://code.activestate.com/recipes/576874/ (r1)
## Upper bound value is at most the length of the longer string.
''' Calculates the Levenshtein distance of 2 strings'''
def editingDistance(s1, s2):
  l1 = len(s1)
  l2 = len(s2)

  matrix = [range(l1 + 1)] * (l2 + 1)
  for zz in range(l2 + 1):
    matrix[zz] = range(zz,zz + l1 + 1)
  for zz in range(0,l2):
    for sz in range(0,l1):
      if s1[sz] == s2[zz]:
        matrix[zz+1][sz+1] = min(matrix[zz+1][sz] + 1, matrix[zz][sz+1] + 1, matrix[zz][sz])
      else:
        matrix[zz+1][sz+1] = min(matrix[zz+1][sz] + 1, matrix[zz][sz+1] + 1, matrix[zz][sz] + 1)
  return matrix[l2][l1]

_reRankWordSplitter = re.compile("\s|\.|-|/|\\|,|:|;|_")

# deprecated
def rankOnSubstrMatches(subStrs, stringOrg):
  stringOrgLen = len(stringOrg)
  rank=0
  while True:
    lcs = longestCommonSubstring(subStrs,stringOrg)
    if len(lcs) >= 3:
      rank+=len(lcs)
      subStrs = subStrs.replace(lcs,'')
      stringOrg = stringOrg.replace(lcs,'')
    else:
      return rank / float(stringOrgLen)

# rank how s fits in text
#   the smaller the number the better it fits, including negative numbers
# If no chars are matching (upper bound editDistance) then 0 is returned
#  best used with all lower cased().
#  and normalized spacing
def rankMe(s, text):
  # complete match, best
  if s == text:
    return -100000
  v = text.find(s)
  # sub string on word boundary, next best
  if v != -1 and(v==0 or text[v-1]==' '):
    # penalize for extra chars
    return -100000+len(text)-len(s)
  textWords = set(_reRankWordSplitter.split(text))
  sWords = _reRankWordSplitter.split(s)
  nrWordsinText = 0
  for w in sWords:
    nrWordsinText += w in textWords
  if nrWordsinText:
    # rank for number of words matched
    # penalize for larger text body
    return (128*-nrWordsinText)+len(text)
  v = editingDistance(s, text)
  if v == max(len(s),len(text)):
     return 0
  return v

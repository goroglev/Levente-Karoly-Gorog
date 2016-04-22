import re

VALID = re.compile(r'[2-9akqjt]{5}')
PAIR = re.compile(r'.*(.).*\1')

def displaymatch(match):
    if not match:
        return None
    assert type(match).__name__ == 'SRE_Match'
    return 'Match: {match}, groups: {groups}'.format(match=match.group(0), groups=match.groups())

def detect_pair(hand):
    if displaymatch(VALID.match(hand)):
        pair = PAIR.search(hand)
        if pair:
            print('{card} appears at least twice in hand `{hand}`'.format(card=pair.group(1), hand=hand))
        else:
            print('No pairs in hand `{hand}`'.format(hand=hand))
    else:
        print('`{hand}` is not a valid hand!'.format(hand=hand))


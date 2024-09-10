import json

with open('../../data_cache/temp.json', 'r') as data:
    data_dict = json.load(data)

    team_three_roster = data_dict['teams'][2]['roster']['entries']
    for i in range(len(team_three_roster)):
        player = team_three_roster[i]['playerPoolEntry']['player']
        if 'injuryStatus' in player:
            if player['injured'] or player['injuryStatus'] != 'ACTIVE':
                print(f"{player['fullName']} : {player['injuryStatus']}")
            else:
                print(f"{player['fullName']}")
        else:
            print(f"{player['fullName']}")



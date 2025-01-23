import React from "react"

import { IconChartAreaLine, IconFriends, IconRuler2, IconTopologyStar3, IconUserCheck, IconMessage, IconChartLine } from "@tabler/icons-react";
import { Group, Stack, Text, Space, Divider} from "@mantine/core";

function Feature({name,summary,icon=<IconMessage/>}) {
  return (
    <Stack>
      <Stack>
        <Group>
        {icon}
        <Text span>{name}</Text>
        </Group>
      </Stack>
      <Text ml="45px">{summary}</Text>
    </Stack>
  )

}

export default function Intro() {

  return (
    <>

    <Text size="xl"> Welcome to Flummox! A platform to play boggle online</Text>
    <Space h="md"/>
    <Text>
      This website was inspired by <a href="https://bogglelive.com/">wordshake.com</a>
      &nbsp;
      and borne out of some frustrations with there implementation and
      the desire for new features. Chief among them, an inability to use textual input
      to submit found words. A guiding principle for this website
      is freedom, freedom to expand the rules of the traditional game
      and create a bespoke competitve word game environment.
    </Text>
    <Space h="md"/>
    <Divider/>
    <Space h="xl"/>
    <Stack justify="flex-start">
      <Feature 
        name="Public & Private User Lobbies" 
        summary={`
          Flummox's lobby system allows each user to create their own lobbies
          and customize them to their liking. Lobbies can be made private, and
          owners can generate invite links to invite their friends.
        `}
        icon=<IconFriends/>
      />
      <Feature 
        name="Lobby Chat Room" 
        summary={`
          Inside a lobby users can exchange messages. The lobby chat will alert
          users of happenings in the lobby.
        `}
        icon=<IconMessage/>
      />
      <Feature 
        name="Board Topology Variations" 
        summary={`
          With some mental gymnastics we can visualize boggle being
          played on different surfaces. We currently support "Planes", "Cylinders",
          and "Tori"
        `}
        icon=<IconTopologyStar3/>
      />
      <Feature 
        name="Scoring Rulesets" 
        summary={`
          Departing from the traditional scoring rule of "Unique" words only,
          Flummox supports "First Finder" and "Any" rulesets.
        `}
        icon=<IconRuler2/>
      />
      <Feature 
        name="Word Knowledge Visualization" 
        summary={` *WIP* After a game ends, a grid of all known words is
          displayed. You can investigate each word and learn about it's relation
          to your performance. This feature will show the anagram group it belongs
          to and your completion percentage of it, similarly it will show the lexeme
          of the word and your percentage of completion.
        `}
        icon=<IconChartAreaLine/>
      />
      <Feature 
        name="User Accounts" 
        summary={`
          *WIP* You can create a persistent user account to track the games you play
          and claim your own custom user name, instead of the default generated one.
        `}
        icon=<IconUserCheck/>
      />
      <Feature 
        name="User Analytics" 
        summary={`
          *Planned* Flummox intends to implement a comprehensive performance
          analytics page. For User Accounts, we will compute words that seem to be
          out of your 'Boggle Lexicon' and suggest words for you to incorporate to
          improve your performance.
        `}
        icon=<IconUserCheck/>
      />
    </Stack>

      {/*

      <IconMessage/>
      <IconTopologyStar3/>
      <IconRuler2/>
      <IconChartAreaLine/>
      <IconUserCheck/>

    */}

    </>
  )

}

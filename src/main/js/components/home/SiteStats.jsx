import React, { useState, useEffect, } from 'react';
import { Stack, Text } from '@mantine/core';
import { GetStats } from "/src/main/js/services/flummox/StatsService.ts";


export default function SiteStats(props) {

  const [stats,setStats] = useState(null)

  const fetchStats = () => {
    GetStats().then(
      (result) => {
        console.log("getting site stats")
        setStats(result.data)
        console.log(result.data)
      },
      () => { 
        console.log("failed to get stats")
      }
    )
  }

  useEffect(() => {
    fetchStats()
  },[]);

  return (
    <Stack h="100%" justify="flex-start" mx="2%">
      <Text>Users Online {stats?.onlineUserCount ?? "N/A"}</Text>
      <Text>Active Lobbies {stats?.lobbiesCount ?? "N/A"}</Text>
      <Text>Games Played {stats?.gamesPlayed ?? "N/A"}</Text>
    </Stack>
  )
}

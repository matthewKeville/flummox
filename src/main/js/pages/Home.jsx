import React from 'react';

import { Grid, Stack } from "@mantine/core";

export default function Home() {

  return (
    <Grid style={{margin: "5%"}}>
      <Grid.Col span={7}>
        <Stack>
          <p> Blog Entry 0 </p>
          <p> Blog Entry 1 </p>
          <p> Blog Entry 2 </p>
          <p> Blog Entry 3 </p>
        </Stack>
      </Grid.Col>
      <Grid.Col span={3}>Stats</Grid.Col>
    </Grid>
  )

}

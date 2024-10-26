import React, { useState } from 'react';
import { TextInput } from "@mantine/core";

export default function WordInput({onWordInput}) {

  const [text, setText] = useState("")

  const processKeyDown = (event) => {
    if  ( event.key == 'Enter' ) {

      let outcome = onWordInput(text)
      setText("")

    }
  }
  return (
    <TextInput value={text} onChange={e => setText(e.target.value)} onKeyDown={processKeyDown}/>
  )
}

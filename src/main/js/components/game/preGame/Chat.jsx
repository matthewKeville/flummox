import React, { useState, useEffect, useRef } from 'react';
import { Stack, Text, Space, ScrollArea } from "@mantine/core";
import WordInput from "/src/main/js/components/WordInput.jsx";
import { SendLobbyChat } from "/src/main/js/services/flummox/LobbyService.ts";

export default function Chat({lobby}) {

  const [messages,setMessages] = useState(null)

  //https://mantine.dev/core/scroll-area/
  //const viewport = useRef<HTMLDivElement>(null);
  const viewport = useRef(null);

  async function onSubmitMessage(message) {
    var serviceResponse = await SendLobbyChat(lobby.id,{ message : message })
  }
  
  //const scrollToBottom = () => viewport.current.scrollTo({ top: viewport.scrollHeight, behavior: 'smooth' });
  const scrollToBottom = () => viewport.current.scrollTo({ top: viewport.current.scrollHeight, behavior: 'smooth' });

  useEffect(() => {

    const evtSource = new EventSource("/api/lobby/"+lobby.id+"/messages/sse")
    evtSource.addEventListener("update", (e) => {
      let data = JSON.parse(e.data)
      console.log("new message data recieved");
      console.log(data)
      setMessages(data)
      scrollToBottom()
    });

    evtSource.addEventListener("init", (e) => {
      let data = JSON.parse(e.data)
      console.log("init message data recieved");
      console.log(data)
      setMessages(data)
    });

    return () => {
      console.log("closing the event source") 
      evtSource.close()
    }

  },[]);
  
  if (messages == null) {
    return <> no message </>
  }

  //<Stack w="480px" h="320px" align="flex-start" justify="flex-end" gap="xs" bd="2px solid black">

  return (

    <>
      <Stack align="flex-start" justify="flex-end" gap="xs" bd="2px solid black">
        <ScrollArea w="480px" h="300px" viewportRef={viewport}>
          {messages.map( (msg,i) => 
            {
              if ( msg.system ) {
                return ( <Text c="red" key={i} >{msg.message}</Text>)
              } else {
                let time = new Date(msg.sent)
                return (
                  <Text key={i}>
                    <Text c="yellow" span>{`${time.getHours().toString().padStart(2,"0")}:${time.getMinutes().toString().padStart(2,"0")}`}</Text>
                    <Text ml="4px" c="green" span>{msg.username}</Text>
                    <Text ml="8px" c="blue"span>{msg.message}</Text>
                  </Text>
                )

              }
            }
          )}
        </ScrollArea>
        <WordInput h="20px" onWordInput={onSubmitMessage}/>
      </Stack>
    </>
  )

}

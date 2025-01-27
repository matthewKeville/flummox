import React, { useState, useEffect, useRef } from 'react';
import { Stack, Text, Table, Space, ScrollArea } from "@mantine/core";
import WordInput from "/src/main/js/components/WordInput.jsx";
import { SendLobbyChat } from "/src/main/js/services/flummox/LobbyService.ts";

export default function Chat({lobby,w="50%",h="50%"}) {

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
      console.log(data)
      scrollToBottom()
    });

    return () => {
      console.log("closing the event source") 
      evtSource.close()
    }

  },[]);
  
  if (messages == null) {
    return <> no message </>
  }

  const messageRows = 
      messages.map( (msg,i) => 
        {
          if ( msg.system ) {

            return (<Text span c="red">{msg.message}</Text>)

          } else {

            let time = new Date(msg.sent)

            /* this is awful, I really need to get better at css*/
            let userString = msg.username
            const userMaxChars = 40

            let userMessageFill = <></>
            if ( userString.length >= userMaxChars ) {
              userString = userString.substring(0,userMaxChars)
            } else {
              let userMessageFill = [...Array(userMaxChars-userString.length).keys()].map( (i) => {
                return (<Space key={userMessageFill+i}  w="xs"/>)
              })
            }

            return (
              <div style={{display: 'flex'}}>
                <Text span c="yellow" >{`${time.getHours().toString().padStart(2,"0")}:${time.getMinutes().toString().padStart(2,"0")}`}</Text>
                <Space w="xs"/>
                <Text span c="green">{msg.username}</Text>
                {userMessageFill}
                <Space w="xs"/>
                <Text span c="blue">{msg.message}</Text>
              </div>
            )
          }
      })

  return (

    
    <>
      <Stack w={w} h={h} align="flex-start" justify="flex-end" gap="xs" bd="2px solid black">
        <ScrollArea w="100%" h="90%" viewportRef={viewport} >
          {messageRows}
        </ScrollArea>
        <WordInput radius="xs" h="10%" w="100%" /*rem*/ onWordInput={onSubmitMessage}/>

      </Stack>
    </>
  )

}

import React, { useState } from 'react';

export default function WordInput({onWordInput}) {

  const [text, setText] = useState("")

  const processKeyDown = (event) => {
    if  ( event.key == 'Enter' ) {

      let outcome = onWordInput(text)
      setText("")

    }
  }
  return (
    <div className="word-input-container">
      <input className="word-input" type="text" value={text} onChange={e => setText(e.target.value)} onKeyDown={processKeyDown} />
    </div>
  )
}

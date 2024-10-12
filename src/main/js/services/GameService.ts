interface GameAnswer {
  answerText : string
}

interface GameAnswerResult {
  success: boolean,
  successMessage?: string,
  failMessage?: string
}

export async function GetPostGameUserSummary(gameId: number) : Promise<ServiceResponse<any>> {

  const response = await fetch("/api/game/" + gameId + "/summary/post");
  let content = await response.json()

  if (response.status == 200 || response != null) {
    console.log('GetPostGameSummary success')
    return {
      data: content,
      success:  true,
      errorMessage: undefined
    }
  }

  else {
    console.log('GetPostGameSummary failed')
    return {
      data: undefined,
      success:  false,
      errorMessage: "unable to get Post Game Summary"
    }
  }

}

export async function GetGameUserSummary(gameId: number) : Promise<ServiceResponse<any>> {

  const response = await fetch("/api/game/" + gameId + "/summary");
  let content = await response.json()

  if (response.status == 200 || response != null) {
    console.log('GetGameUserSummary success')
    return {
      data: content,
      success:  true,
      errorMessage: undefined
    }
  }

  else {
    console.log('GetGameUserSummary failed')
    return {
      data: undefined,
      success:  false,
      errorMessage: "unable to get Game Summary"
    }
  }

}

/*
  * This endpoint needs to be rewritten, we should not use the response code 200
  * to indicate a successful answer. It should indicate a successful request,
  * answer validity and the reason for rejection should be in the response data model.
  */
export async function PostGameAnswer(lobbyId: number, answer: GameAnswer) : Promise<ServiceResponse<GameAnswerResult>> {

    let answerBody : any = {}
    answerBody.answer = answer.answerText

    const response = await fetch("/api/game/" + lobbyId + "/answer", {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(answerBody)
    });

    if (response.status == 200) {
      console.log('PostGameAnswer Success')
      return {
        data: {
          success: true,
          successMessage: "nice",
          failMessage: undefined
        },
        success:  true,
        errorMessage: undefined
      }
    } else {

      //TBD when data model changes !200 -> ServiceRequest.success = fail

      const content = await response.json();
      let failMessage = ""

      switch (content.message) {
        case "INVALID_ANSWER":
          failMessage = " nope ."
          break;
        case "ANSWER_ALREADY_FOUND":
          failMessage = " word already found ..."
          break;
        case "GAME_OVER":
          failMessage = " game is over ... "
          break;
        case "INTERNAL_ERROR":
        default:
          failMessage = content.status + " : Unknown error"
      }

      return {
        data: {
          success: false,
          successMessage: undefined,
          failMessage: failMessage
        },
        success:  true,
        errorMessage: undefined
      }


    }
}


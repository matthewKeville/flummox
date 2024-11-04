//webpack alias resolves to config/local.json or  config/prod.json
import config from "config" 

interface GameAnswer {
  answerText : string
}

interface GameAnswerResult {
  success: boolean,
  successMessage?: string,
  failMessage?: string
}

export async function GetPostGame(gameId: number) : Promise<ServiceResponse<any>> {

  const response = await fetch(config.origin+"/api/game/" + gameId + "/post");
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

/*
  * This endpoint needs to be rewritten, we should not use the response code 200
  * to indicate a successful answer. It should indicate a successful request,
  * answer validity and the reason for rejection should be in the response data model.
  */
export async function PostGameAnswer(gameId: number, userId: number,answer: GameAnswer) : Promise<ServiceResponse<GameAnswerResult>> {

    let answerBody : any = {}
    answerBody.answer = answer.answerText

    const response = await fetch(config.origin+"/api/game/" + gameId + "/answer/" + userId, {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(answerBody)
    });
    
    console.log(response.redirected)
    console.log("kajdsfkajsdf")

    if (response.ok ) {
      let data = await response.json()
      console.log('PostGameAnswer Success')
      console.log(data);
      return {
        data: data,
        success:  true,
        errorMessage: undefined
      }
    }

    return null;
}


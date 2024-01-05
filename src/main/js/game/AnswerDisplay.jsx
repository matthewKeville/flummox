import React from 'react';

export default function AnswerDisplay({words}) {
  return (
    <div className="word-columns">
      <div className="three">
        {words.filter( w => w.length == 3).map( w => (<div key={w} className="found-word">{w}</div>) )}
      </div>
      <div className="four">
        {words.filter( w => w.length == 4).map( w => (<div key={w} className="found-word">{w}</div>) )}
      </div>
      <div className="five">
        {words.filter( w => w.length == 5).map( w => (<div key={w} className="found-word">{w}</div>) )}
      </div>
      <div className="six">
        {words.filter( w => w.length == 6).map( w => (<div key={w} className="found-word">{w}</div>) )}
      </div>
      <div className="seven">
        {words.filter( w => w.length == 7).map( w => (<div key={w} className="found-word">{w}</div>) )}
      </div>
      <div className="eight-plus">
        {words.filter( w => w.length >= 8).map( w => (<div key={w} className="found-word">{w}</div>) )}
      </div>
    </div>
  );
}

import React from 'react';

export default function ScoreboardUserDisplay({scoreboardEntry,username}) {

    return (
        <div className="scoreboard-entry-flex">
            <div className="scoreboard-entry-rank">{scoreboardEntry.rank}</div>
            <div className="scoreboard-entry-username">{username}</div>
            <div className="scoreboard-entry-score">{scoreboardEntry.score}</div>
            <div className="scoreboard-entry-word-count">{scoreboardEntry.wordsFound}</div>
        </div>
    );
}


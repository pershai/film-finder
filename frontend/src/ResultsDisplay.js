import React from 'react';

function ResultsDisplay({ movies }) {
  return (
    <div>
      <h2>Results</h2>
      <pre>{movies}</pre>
    </div>
  );
}

export default ResultsDisplay;

import React, { useState } from 'react';
import './App.css';
import SearchBar from './SearchBar';
import ResultsDisplay from './ResultsDisplay';

function App() {
  const [movies, setMovies] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSearch = async (query) => {
    setLoading(true);
    try {
      const response = await fetch(`/api/movies/find?query=${query}`);
      const data = await response.text();
      setMovies(data);
    } catch (error) {
      console.error('Error fetching movies:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>Film Finder</h1>
      </header>
      <main>
        <SearchBar onSearch={handleSearch} />
        {loading ? <p>Loading...</p> : <ResultsDisplay movies={movies} />}
      </main>
    </div>
  );
}

export default App;

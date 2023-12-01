import React from 'react';
import { useLoaderData, Link } from "react-router-dom";

export default function NavBar() {
  return (
    <>
    <Link className="nav-link" to="/">Home</Link>
    <Link className="nav-link" to="/lobby">Lobbies</Link>
    </>
  )
}

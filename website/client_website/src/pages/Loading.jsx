import React from "react";
import "../styles/pages/loading.css"; 

export default function Loading() {
  return (
    <div className="loading-wrapper">
      <div className="loading-glow"></div>

      <div className="loading-logo">
        <div className="circle outer"></div>
        <div className="circle inner"></div>

        <img
          src="/assets/anime.jpg"
          alt="MobileHub Anime"
          className="loading-anime"
        />
      </div>

      <span className="loading-text">MobileHub</span>

      <div className="loading-dots">
        <span>.</span>
        <span>.</span>
        <span>.</span>
      </div>
    </div>
  );
}
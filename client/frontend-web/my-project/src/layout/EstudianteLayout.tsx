// src/layouts/ProfesorLayout.tsx
import React from "react";
import SidebarEstudiante from "../components/estudiante/SidebarEstudiante";
import { Outlet } from "react-router-dom";

const EstudianteLayout: React.FC = () => (
  <div className="flex min-h-screen bg-gray-100">
    <SidebarEstudiante />
    <main className="flex-1 p-8">
      <Outlet />
    </main>
  </div>
);

export default EstudianteLayout;

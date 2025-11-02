// src/layouts/ProfesorLayout.tsx
import React from "react";
import SidebarProfesor from "../components/profesor/SidebarProfesor";
import { Outlet } from "react-router-dom";

const ProfesorLayout: React.FC = () => (
  <div className="flex min-h-screen bg-gray-100">
    <SidebarProfesor />
    <main className="flex-1 p-8">
      {/* El componente cargado dinámicamente */}
      <Outlet />
    </main>
  </div>
);

export default ProfesorLayout;

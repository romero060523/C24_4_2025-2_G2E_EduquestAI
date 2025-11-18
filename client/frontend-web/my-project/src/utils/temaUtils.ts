// Utilidades para obtener imágenes y estilos según el tema visual

export type TemaVisual = 'MEDIEVAL' | 'ANIME' | 'ESPACIAL' | 'FANTASIA' | 'CIENCIA' | 'NATURALEZA' | 'URBANO' | 'OCEANO' | 'DEFAULT';

export interface TemaConfig {
  bg: string;
  border: string;
  icon: string;
  gradient: string;
  headerBg: string;
  textColor: string;
  imagePath: string;
  cardBg: string;
}

export const getTemaConfig = (tema: TemaVisual = "DEFAULT"): TemaConfig => {
  const temas: Record<TemaVisual, TemaConfig> = {
    MEDIEVAL: {
      bg: "bg-gradient-to-br from-amber-900 via-amber-800 to-amber-700",
      border: "border-amber-600",
      icon: "🏰",
      gradient: "from-amber-800 to-amber-600",
      headerBg: "bg-gradient-to-r from-amber-900 to-amber-700",
      textColor: "text-amber-50",
      imagePath: "/images/temas/medieval/",
      cardBg: "bg-gradient-to-br from-amber-50 to-amber-100",
    },
    ANIME: {
      bg: "bg-gradient-to-br from-pink-500 via-purple-600 to-indigo-700",
      border: "border-pink-400",
      icon: "🎌",
      gradient: "from-pink-400 to-purple-500",
      headerBg: "bg-gradient-to-r from-pink-500 to-purple-600",
      textColor: "text-pink-50",
      imagePath: "/images/temas/anime/",
      cardBg: "bg-gradient-to-br from-pink-50 to-purple-50",
    },
    ESPACIAL: {
      bg: "bg-gradient-to-br from-indigo-900 via-blue-900 to-purple-900",
      border: "border-indigo-400",
      icon: "🚀",
      gradient: "from-indigo-800 to-blue-800",
      headerBg: "bg-gradient-to-r from-indigo-900 to-blue-900",
      textColor: "text-indigo-50",
      imagePath: "/images/temas/espacial/",
      cardBg: "bg-gradient-to-br from-indigo-50 to-blue-50",
    },
    FANTASIA: {
      bg: "bg-gradient-to-br from-purple-700 via-pink-600 to-rose-600",
      border: "border-purple-400",
      icon: "✨",
      gradient: "from-purple-600 to-pink-500",
      headerBg: "bg-gradient-to-r from-purple-700 to-pink-600",
      textColor: "text-purple-50",
      imagePath: "/images/temas/fantasia/",
      cardBg: "bg-gradient-to-br from-purple-50 to-pink-50",
    },
    CIENCIA: {
      bg: "bg-gradient-to-br from-cyan-600 via-blue-700 to-indigo-800",
      border: "border-cyan-400",
      icon: "🔬",
      gradient: "from-cyan-500 to-blue-600",
      headerBg: "bg-gradient-to-r from-cyan-600 to-blue-700",
      textColor: "text-cyan-50",
      imagePath: "/images/temas/ciencia/",
      cardBg: "bg-gradient-to-br from-cyan-50 to-blue-50",
    },
    NATURALEZA: {
      bg: "bg-gradient-to-br from-green-700 via-emerald-800 to-teal-900",
      border: "border-green-400",
      icon: "🌿",
      gradient: "from-green-600 to-emerald-700",
      headerBg: "bg-gradient-to-r from-green-700 to-emerald-800",
      textColor: "text-green-50",
      imagePath: "/images/temas/naturaleza/",
      cardBg: "bg-gradient-to-br from-green-50 to-emerald-50",
    },
    URBANO: {
      bg: "bg-gradient-to-br from-gray-700 via-gray-800 to-gray-900",
      border: "border-gray-400",
      icon: "🏙️",
      gradient: "from-gray-600 to-gray-800",
      headerBg: "bg-gradient-to-r from-gray-700 to-gray-900",
      textColor: "text-gray-50",
      imagePath: "/images/temas/urbano/",
      cardBg: "bg-gradient-to-br from-gray-50 to-gray-100",
    },
    OCEANO: {
      bg: "bg-gradient-to-br from-blue-600 via-cyan-700 to-teal-800",
      border: "border-blue-400",
      icon: "🌊",
      gradient: "from-blue-500 to-cyan-600",
      headerBg: "bg-gradient-to-r from-blue-600 to-cyan-700",
      textColor: "text-blue-50",
      imagePath: "/images/temas/oceano/",
      cardBg: "bg-gradient-to-br from-blue-50 to-cyan-50",
    },
    DEFAULT: {
      bg: "bg-gradient-to-br from-blue-500 via-indigo-600 to-purple-700",
      border: "border-blue-300",
      icon: "📚",
      gradient: "from-blue-400 to-indigo-500",
      headerBg: "bg-gradient-to-r from-blue-500 to-indigo-600",
      textColor: "text-blue-50",
      imagePath: "/images/temas/default/",
      cardBg: "bg-gradient-to-br from-blue-50 to-indigo-50",
    },
  };
  return temas[tema] || temas.DEFAULT;
};

// Función para obtener la URL de la imagen de fondo
export const getTemaImage = (tema: TemaVisual, imageName: string = "header-bg.jpg"): string => {
  const config = getTemaConfig(tema);
  return `${config.imagePath}${imageName}`;
};



import { useState, useRef, useEffect } from "react";
import { Send, Sparkles, Book, Video, FileText, Lightbulb, TrendingUp } from "lucide-react";
import { useAuth } from "../../hooks/useAuth";
import ReactMarkdown from "react-markdown";

interface Message {
  id: string;
  text: string;
  sender: "user" | "ai";
  timestamp: Date;
}

interface QuickSuggestion {
  icon: React.ReactNode;
  title: string;
  description: string;
}

interface RecentTopic {
  title: string;
  time: string;
}

const ChatIAPage = () => {
  const { usuario } = useAuth();
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "1",
      text: "¡Hola Ana! 👋 Soy tu asistente educativo IA. ¿En qué puedo ayudarte hoy?",
      sender: "ai",
      timestamp: new Date(),
    },
  ]);
  const [inputValue, setInputValue] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [conversacionId, setConversacionId] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const quickSuggestions: QuickSuggestion[] = [
    {
      icon: <Book className="w-5 h-5 text-blue-600" />,
      title: "Recursos de matemáticas",
      description: "Obtén ayuda con ecuaciones y problemas",
    },
    {
      icon: <Video className="w-5 h-5 text-purple-600" />,
      title: "Videos de repaso",
      description: "Encuentra videos educativos",
    },
    {
      icon: <FileText className="w-5 h-5 text-green-600" />,
      title: "Resúmenes de literatura",
      description: "Comprende mejor las lecturas",
    },
    {
      icon: <Lightbulb className="w-5 h-5 text-yellow-600" />,
      title: "Ayuda con tareas",
      description: "Guía para resolver ejercicios",
    },
  ];

  const recentTopics: RecentTopic[] = [
    { title: "Ecuaciones cuadráticas", time: "Hace 5 minutos" },
    { title: "Verbos irregulares en inglés", time: "Ayer" },
    { title: "Segunda Guerra Mundial", time: "Hace 3 días" },
  ];

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = async () => {
    if (!inputValue.trim() || !usuario) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      text: inputValue,
      sender: "user",
      timestamp: new Date(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInputValue("");
    setIsLoading(true);

    try {
      const requestBody = {
        mensaje: userMessage.text,
        conversacionId: conversacionId,
        usuarioId: usuario.id,
        rolUsuario: usuario.rol,
      };

      const response = await fetch(`http://localhost:8081/api/v1/chat`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(requestBody),
      });

      if (!response.ok) {
        throw new Error("Error en la respuesta de la IA");
      }

      const data = await response.json();

      if (!conversacionId && data.conversacionId) {
        setConversacionId(data.conversacionId);
      }

      const aiMessage: Message = {
        id: data.mensajeId || (Date.now() + 1).toString(),
        text: data.respuesta,
        sender: "ai",
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, aiMessage]);
    } catch (error) {
      console.error("Error:", error);
      const errorMessage: Message = {
        id: (Date.now() + 1).toString(),
        text: "Lo siento, hubo un error al procesar tu mensaje. Por favor, intenta de nuevo.",
        sender: "ai",
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSuggestionClick = (suggestion: QuickSuggestion) => {
    setInputValue(suggestion.title);
  };

  return (
    <div className="flex h-[calc(100vh-2rem)] gap-6">
      {/* Main Chat Area */}
      <div className="flex-1 flex flex-col bg-white rounded-2xl shadow-sm border border-gray-200">
        {/* Header */}
        <div className="p-6 border-b border-gray-200">
          <h1 className="text-2xl font-bold text-gray-900">Chat IA</h1>
          <p className="text-sm text-gray-500 mt-1">
            Tu asistente educativo personalizado
          </p>
        </div>

        {/* Messages Area */}
        <div className="flex-1 overflow-y-auto p-6 space-y-4">
          {messages.map((msg) => (
            <div
              key={msg.id}
              className={`flex ${
                msg.sender === "user" ? "justify-end" : "justify-start"
              }`}
            >
              <div
                className={`max-w-[70%] ${
                  msg.sender === "user" ? "order-2" : "order-1"
                }`}
              >
                {msg.sender === "ai" && (
                  <div className="flex items-center gap-2 mb-2">
                    <div className="w-8 h-8 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
                      <Sparkles className="w-4 h-4 text-white" />
                    </div>
                    <span className="text-xs text-gray-500 font-medium">
                      Asistente IA
                    </span>
                    <span className="text-xs text-green-600 bg-green-50 px-2 py-0.5 rounded-full">
                      ● En línea
                    </span>
                  </div>
                )}
                <div
                  className={`p-4 rounded-2xl ${
                    msg.sender === "user"
                      ? "bg-blue-600 text-white rounded-tr-none"
                      : "bg-gray-50 text-gray-800 rounded-tl-none border border-gray-200"
                  }`}
                >
                  {msg.sender === "ai" ? (
                    <div className="text-sm leading-relaxed prose prose-sm max-w-none markdown-content">
                      <ReactMarkdown
                        components={{
                          p: ({ children }) => <p className="mb-2 last:mb-0">{children}</p>,
                          strong: ({ children }) => <strong className="font-semibold text-gray-900">{children}</strong>,
                          em: ({ children }) => <em className="italic">{children}</em>,
                          ul: ({ children }) => <ul className="list-disc list-inside mb-2 space-y-1">{children}</ul>,
                          ol: ({ children }) => <ol className="list-decimal list-inside mb-2 space-y-1">{children}</ol>,
                          li: ({ children }) => <li className="ml-2">{children}</li>,
                          code: ({ children }) => <code className="bg-gray-200 px-1.5 py-0.5 rounded text-xs font-mono">{children}</code>,
                        }}
                      >
                        {msg.text}
                      </ReactMarkdown>
                    </div>
                  ) : (
                    <p className="text-sm whitespace-pre-wrap leading-relaxed">
                      {msg.text}
                    </p>
                  )}
                </div>
                <span className="text-xs text-gray-400 mt-1 block">
                  {msg.timestamp.toLocaleTimeString("es-ES", {
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </span>
              </div>
            </div>
          ))}
          {isLoading && (
            <div className="flex justify-start">
              <div className="flex items-center gap-2 bg-gray-50 px-4 py-3 rounded-2xl rounded-tl-none border border-gray-200">
                <div className="flex gap-1">
                  <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: "0ms" }}></div>
                  <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: "150ms" }}></div>
                  <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: "300ms" }}></div>
                </div>
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        {/* Input Area */}
        <div className="p-6 border-t border-gray-200 bg-gray-50">
          <div className="flex gap-3">
            <input
              type="text"
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSendMessage()}
              placeholder="Escribe tu mensaje..."
              className="flex-1 px-4 py-3 rounded-xl bg-white border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none text-sm"
            />
            <button
              onClick={handleSendMessage}
              disabled={isLoading || !inputValue.trim()}
              className="px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2 font-medium"
            >
              <Send className="w-4 h-4" />
              Enviar
            </button>
          </div>
        </div>
      </div>

      {/* Right Sidebar */}
      <div className="w-80 space-y-6">
        {/* Quick Suggestions */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            Sugerencias rápidas
          </h3>
          <div className="space-y-3">
            {quickSuggestions.map((suggestion, index) => (
              <button
                key={index}
                onClick={() => handleSuggestionClick(suggestion)}
                className="w-full flex items-start gap-3 p-3 rounded-xl hover:bg-gray-50 transition-colors text-left border border-gray-100 hover:border-blue-200"
              >
                <div className="flex-shrink-0 mt-0.5">{suggestion.icon}</div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900">
                    {suggestion.title}
                  </p>
                  <p className="text-xs text-gray-500 mt-0.5">
                    {suggestion.description}
                  </p>
                </div>
              </button>
            ))}
          </div>
        </div>

        {/* Recent Topics */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            Temas recientes
          </h3>
          <div className="space-y-3">
            {recentTopics.map((topic, index) => (
              <div
                key={index}
                className="flex items-start gap-3 p-3 rounded-xl hover:bg-gray-50 transition-colors cursor-pointer"
              >
                <TrendingUp className="w-4 h-4 text-gray-400 mt-0.5 flex-shrink-0" />
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900 truncate">
                    {topic.title}
                  </p>
                  <p className="text-xs text-gray-500 mt-0.5">{topic.time}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Daily Tip */}
        <div className="bg-gradient-to-br from-blue-50 to-purple-50 rounded-2xl shadow-sm border border-blue-100 p-6">
          <div className="flex items-center gap-2 mb-3">
            <Sparkles className="w-5 h-5 text-blue-600" />
            <h3 className="text-lg font-semibold text-gray-900">Tip del día</h3>
          </div>
          <p className="text-sm text-gray-700 leading-relaxed">
            Puedo recomendarte recursos personalizados basados en tu progreso y
            estilo de aprendizaje.
          </p>
        </div>
      </div>
    </div>
  );
};

export default ChatIAPage;

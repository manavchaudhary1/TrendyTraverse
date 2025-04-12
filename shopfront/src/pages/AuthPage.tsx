
import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { 
  Tabs, 
  TabsContent, 
  TabsList, 
  TabsTrigger 
} from '@/components/ui/tabs';
import LoginForm from '@/components/auth/LoginForm';
import SignupForm from '@/components/auth/SignupForm';
import { ShoppingBag } from 'lucide-react';

const AuthPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const defaultTab = location.pathname === '/signup' ? 'signup' : 'login';
  const [activeTab, setActiveTab] = useState(defaultTab);

  // Update URL based on tab without full page reload
  const handleTabChange = (value: string) => {
    setActiveTab(value);
    navigate(value === 'signup' ? '/signup' : '/login', { replace: true });
  };

  return (
    <div className="container mx-auto px-4 py-12">
      <div className="max-w-md mx-auto">
        {/* Logo */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center">
            <ShoppingBag className="h-10 w-10 text-purple-600" />
            <span className="ml-2 text-2xl font-bold">TrendyTraverse</span>
          </div>
        </div>

        {/* Auth Tabs */}
        <Tabs
          defaultValue={activeTab}
          value={activeTab}
          onValueChange={handleTabChange}
          className="w-full"
        >
          <TabsList className="grid w-full grid-cols-2 mb-8">
            <TabsTrigger value="login">Login</TabsTrigger>
            <TabsTrigger value="signup">Sign Up</TabsTrigger>
          </TabsList>
          <TabsContent value="login">
            <LoginForm />
          </TabsContent>
          <TabsContent value="signup">
            <SignupForm />
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default AuthPage;
